#include <unistd.h>
#include <assert.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include "api.h"

#include "BTrack.h"

constexpr const char* MONITOR_SOURCE = "alsa_output.platform-bcm2835_audio.analog-stereo.monitor";

int spawn_child() {
    int pipefd[2];
    assert(pipe(pipefd) == 0);

    if(fork()) {
        assert(dup2(pipefd[1], STDOUT_FILENO) >= 0);
        assert(close(pipefd[1]) == 0);
        execlp("/usr/bin/parec", "parec", "--format=s16", "--rate=44100", "-d", MONITOR_SOURCE, NULL);

        perror("execlp");

        return 0;
    } else {
        return pipefd[0];
    }
}

int read_frame(int16_t* frame, int pipefd, size_t framesize) {
    char *readptr = (char*) frame;
    size_t bytes_left = framesize * sizeof(int16_t);
    while(bytes_left > 0) {
        int did_read = read(pipefd, readptr, bytes_left);
        if(did_read < 0) {
            perror("read");
            return -1;
        }
        bytes_left -= did_read;
        readptr += did_read;
    }
    return 0;
}

int16_t average_frame(int16_t* frame, size_t framesize) {
    int16_t average = 0;
    for(int i = 0; i < framesize; ++i) {
        average += (abs(frame[i]) / framesize);
    }
    return average;
}

int main() {
    ProcessedFrame outval;
    BTrack b;
    assert(sizeof(double) == 8);
    assert(sizeof(float) == 4);

    double *dframe = new double[1024];
    int16_t *iframe = new int16_t[1024];

    int pipefd = spawn_child();

    while(true) {
        if (read_frame(iframe, pipefd, 1024) < 0) {
            return 1;
        }
        for(int i = 0; i < 1024; ++i) {
            dframe[i] = iframe[i];
        }

        b.processAudioFrame(dframe);
        outval.isBeat = b.beatDueInCurrentFrame();
        outval.frameAverage = average_frame(iframe, 1024);

        assert(outval.frameAverage >= 0);

        assert(write(STDOUT_FILENO, &outval.frameAverage, sizeof(int16_t)) == sizeof(int16_t));
        assert(write(STDOUT_FILENO, &outval.isBeat, sizeof(uint8_t)) == sizeof(uint8_t));
    }

    return 0;
}
