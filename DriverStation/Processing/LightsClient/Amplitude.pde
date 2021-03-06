public class Amplitude implements Animation {
  private PApplet window;
  private Client c;
  private processing.sound.Amplitude amp;
  
  float[] hist;
  int idx = 0;
  
  int avgsamples = 3;
  
  int w = 2;
  
  float amplification;
  
  int elbow1 = 37, elbow2 = 76, size = 118;
  
  public Amplitude(PApplet window, Client c, AudioIn mic, float amplification) {
    this.window = window;
    this.c = c;
    this.amp = new processing.sound.Amplitude(window);
    amp.input(mic);
    c.sendPacket(PacketFactory.setRefreshTime(30));
    hist  = new float[(int)(width/w)];
    this.amplification = amplification/20;
    
  }
  
  public Amplitude(PApplet window, Client c, SoundFile mic, float amplification) {
    this.window = window;
    this.c = c;
    this.amp = new processing.sound.Amplitude(window);
    amp.input(mic);
    hist  = new float[(int)(width/w)];
    this.amplification = amplification/12;
    
  }
  
  @Override
  public void setup() {
    c.sendPacket(PacketFactory.setRefreshTime(50));
  }
  
  @Override
  public void draw() {
    float data = constrain(amp.analyze() * amplification, 0, 1);
    
    hist[idx] = data;
    
    float avg = 0;
    for(int i = 0, j = idx; i < avgsamples; i++, j = (j-1+hist.length) % hist.length) {
      avg += hist[j];
    }
    avg /= avgsamples;
    
    Color c = ColorTools.mapRainbow(avg);
    
    Command[] frame = new Command[3];
    int level1 = (int)map(1-avg, 0, 1, 0, elbow1);
    int level2 = (int)map(avg, 0, 1, elbow2, size)+1;
    frame[0] = CommandFactory.makeRun(0, ColorTools.rainbow[ColorTools.rainbow.length - 1], level1);
    frame[1] = CommandFactory.makeRun(level1, c, level2);
    frame[2] = CommandFactory.makeRun(level2, ColorTools.rainbow[ColorTools.rainbow.length - 1], size);
    
    this.c.sendFrame(frame);
    
    
    for(int i = hist.length-1, j = idx; i >= 0; i--, j = (j-1+hist.length) % hist.length) {
      window.noStroke();
      c = ColorTools.mapRainbow(hist[j]);
      window.fill(c.getRed(), c.getGreen(), c.getBlue());
      window.rect(i * w, height - (int)(hist[j] * height), w, (int)(hist[j] * height));
    }
    idx = (idx + 1) % hist.length;
  }
}
