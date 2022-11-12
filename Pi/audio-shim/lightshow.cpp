#include "api.h"

#include <stdio.h>
#include <unistd.h>
#include <cassert>

constexpr int ROLLING_AVG_COUNT = 5;
constexpr float GAIN = 4;

void read_frame(ProcessedFrame* frame) {
    assert(read(STDIN_FILENO, &frame->frameAverage, sizeof(int16_t)) == sizeof(int16_t));
    assert(read(STDIN_FILENO, &frame->isBeat, sizeof(uint8_t)) == sizeof(uint8_t));
}

int main() {
    const char* colors[] = {"31", "32", "34", "35", "36"};
    int curr_color_idx = 0;
    char linebuff[80];

    int16_t rollingAvgBuff[ROLLING_AVG_COUNT] = {};
    size_t rollingAvgIdx = 0;

    int16_t average = 0;

    ProcessedFrame frame;

    while(true) {
        read_frame(&frame);

        int16_t oldavg = rollingAvgBuff[rollingAvgIdx];
        rollingAvgBuff[rollingAvgIdx] = frame.frameAverage;
        rollingAvgIdx = (rollingAvgIdx + 1) % ROLLING_AVG_COUNT;

        average += (frame.frameAverage - oldavg) / ROLLING_AVG_COUNT;

        if(frame.isBeat) {
            curr_color_idx = (curr_color_idx + 1) % 5;
        }

        int chars = (((average*GAIN) / 32767.0) * 79) + 1;
        int i;
        for(i = 0; i < 79 && i < chars; ++i) {
            linebuff[i] = '*';
        }
        linebuff[i] = '\0';

        fprintf(stderr, "\33[2K\r");
        fprintf(stderr, "\33[%sm", colors[curr_color_idx]);
        fprintf(stderr, linebuff);
        fflush(stderr);
    }
}
