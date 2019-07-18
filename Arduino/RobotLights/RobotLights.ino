#include <Wire.h>
#include <Adafruit_NeoPixel.h>
#ifdef __AVR__
#include <avr/power.h>
#endif

/*
   I2C controlled NeoPixel driver

   Receives command packets over I2C and allows painting RGB values into NeoPixel strip.

   I2C address: 16

   Packet format:
   - B0 : payload size
   - B1-15 : payload  (Max payload is 15 bytes)

   Command payload format:
   - B0 : command id
   - B1-14 : command data (varies by command)

   Commands:
   - 0x01 : <empty> : show current image
   - 0x02 : pixel address ; RGB : set single pixel at address with value RGB
   - 0x03 : start address ; RGB ; length : set "length" pixels starting at "start" with value RGB
   - 0x04 : start address ; RGB ; length ; stride : set "length" pixels starting at "start" with value RGB.  Repeat every "stride" pixels.
   - 0x05 : start address ; RGB ; length ; stride ; max length : same as 0x04, but will stop after max length pixels

   Error rejection/correction:
   - Invalid packets will be rejected and packet parsing will begin again with next byte in stream.

   Display protocol:
   - At beginning of frame, send >= 16 0xff bytes to force resync
   - Send paint commands to fill image buffer.  Unpainted pixels will retain prior contents.
   - Send "show current image" command

   NeoPixels will hold the last value they received.  There is no required refresh rate.

*/

// Max pixels on Trinket is 94 due to RAM limits

#if defined(ARDUINO_AVR_UNO)
#define PIN 1
#define MAXLEDS 240

#elif defined(ARDUINO_AVR_PRO)
#define PIN 3
#define MAXLEDS 240

#else
#error unsupported board.  Please add to code.
#endif

const uint32_t dimRED = 0x200000;
const uint32_t dimGREEN = 0x002000;
const uint32_t dimBLUE = 0x000020;
const uint32_t dimWHITE = 0x202020;

// Parameter 1 = number of pixels in strip
// Parameter 2 = Arduino pin number (most are valid)
// Parameter 3 = pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    Pixels are wired for RGBW bitstream (NeoPixel RGBW products)

Adafruit_NeoPixel strip = Adafruit_NeoPixel(MAXLEDS, PIN, NEO_GRB + NEO_KHZ800);

// IMPORTANT: To reduce NeoPixel burnout risk, add 1000 uF capacitor across
// pixel power leads, add 300 - 500 Ohm resistor on first pixel's data input
// and minimize distance between Arduino and first pixel.  Avoid connecting
// on a live circuit...if you must, connect GND first.

const int MAX_PAYLOAD = 15;
uint8_t payload[MAX_PAYLOAD];

void setup() {
  strip.begin();
  for (int i = 0; i < strip.numPixels();)
  {
    strip.setPixelColor(i++, dimRED);
    strip.setPixelColor(i++, dimGREEN);
    strip.setPixelColor(i++, dimBLUE);
    strip.setPixelColor(i++, dimWHITE);
  }
  strip.show();       // Initialize all pixels to RGBW test pattern
  Wire.begin(16);     // join i2c bus with address #16
  Wire.onReceive(onReceive);
}

void loop() {
  delay(100);
}

void onReceive(int howMany) {
  static int payloadIx = 0, payloadLen = 0;
  while (Wire.available())
  {
    uint8_t b = Wire.read();
    if (payloadLen == 0) {
      // start of packet state
      if (b > MAX_PAYLOAD) {
        // re-sync
      }
      else {
        // begin new payload
        payloadIx = 0;
        payloadLen = b;
      }
    }
    else {
      // in-packet state
      // receive payload byte
      payload[payloadIx++] = b;
      if (payloadIx == payloadLen) {
        parsePayload(payloadLen);
        payloadLen = 0;
      }
    }
  }
}

// param: len is the total size of the payload, including the command.
// Each command is responsible for checking the payload size for a match.
// In the event of a mismatch, do nothing and discard the payload.
//
void parsePayload(int len) {
  int command = payload[0];
  int address = payload[1];
  uint32_t rgb = getRGB(&payload[2]);

  switch (command) {
    case 1: // display current strip contents
      if (len == 1)
        strip.show();
      break;

    case 2: // set single pixel
      if (len == 5)
        paintPattern(address, rgb, 1, 0, 0);
      break;

    case 3: // set run of pixels
      if (len == 6) {
        int count = payload[5];
        paintPattern(address, rgb, count, 0, 0);
      }
      break;

    case 4: // set run of pixels with stride
      if (len == 7) {
        int count = payload[5];
        int stride = payload[6];
        paintPattern(address, rgb, count, stride, 0);
      }
      break;

    case 5: // set run of pixels with stride and limit
      if (len == 8) {
        int count = payload[5];
        int stride = payload[6];
        int maxlength = payload[7];
        paintPattern(address, rgb, count, stride, address + maxlength);
      }
      break;

    default: // unrecognized command
      break;
  }
}

uint32_t getRGB(uint8_t rgb[]) {
  uint32_t red = rgb[0];
  uint32_t green = rgb[1];
  uint32_t blue = rgb[2];

  // pack RGB in big-endian order
  return (red << 16) | (green << 8) | blue;
}

// stride of 0 will paint once and exit (instead of infinite loop)
// stride >0 will repeat pattern until last pixel (limit)
void paintPattern(int address, uint32_t rgb, int count, int stride, int limit) {
  if (limit == 0 || limit > strip.numPixels())
    limit = strip.numPixels();

  do {
    for (int i = 0; i < count; ++i) {
      int pixel = address + i;
      if (pixel < limit)
        strip.setPixelColor(pixel, rgb);
    }
    address += stride;
  } while (address < limit && stride > 0);
}
