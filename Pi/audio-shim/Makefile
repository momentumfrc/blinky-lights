CXX = g++
CXXFLAGS = -Wall -Werror -Wextra -std=c++11 -pedantic -march=native -pipe
CXXFLAGS += -Wno-error=sign-compare
CXXFLAGS += -O3


BTRACK_INCLUDE = /home/jordan/BTrack/src
BTRACK_LIB_DIR = /home/jordan/BTrack/src
BTRACK_LINKFLAGS = -Wl,-rpath,$(BTRACK_LIB_DIR) \
	-L$(BTRACK_LIB_DIR) \
	-lbtrack

all: lightshow shim

lightshow: lightshow.cpp api.h
	$(CXX) $(CXXFLAGS) lightshow.cpp -o lightshow

shim: shim.cpp api.h
	$(CXX) $(CXXFLAGS) -I$(BTRACK_INCLUDE) shim.cpp $(BTRACK_LINKFLAGS) -o shim
