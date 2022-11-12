#pragma once

#include <stdint.h>

struct ProcessedFrame {
    int16_t frameAverage {0};
    uint8_t isBeat {0};
};
