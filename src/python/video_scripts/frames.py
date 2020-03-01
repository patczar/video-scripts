# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module defines classes and functions to identify and search for individual frames of videos.

from typing import List

class Frame:
    '''Abstract class representing a frame in a multimedia file.'''
    def __init__(self, n, pts, time, pos, checksum=None):
        self.n = int(n)
        self.pts = int(pts)
        self.time = float(time)
        self.pos = int(pos)
        self.checksum = checksum

    def __str__(self):
        return 'frame no %d, pts: %d, time: %f' % (self.n, self.pts , self.time)


class AudioFrame(Frame):
    '''
    An object of this class represents an audio frame from a multimedia file.
    The meaning of this term and the details attached to it are taken from the ffmpeg tool.
    '''
    def __init__(self, n, pts, time, pos, samples, checksum=None):
        super().__init__(n, pts, time, pos, checksum)
        self.samples = int(samples)

    def __str__(self):
        return 'A frame no %d, pts: %d, time: %f, samples: %d' % (self.n, self.pts , self.time, self.samples)


class VideoFrame(Frame):
    '''
    An object of this class represents a video frame from a multimedia file.
    The meaning of this term and the details attached to it are taken from the ffmpeg tool.
    '''
    def __init__(self, n, pts, time, pos, iskey, frame_type, checksum=None):
        super().__init__(n, pts, time, pos, checksum)
        self.iskey = bool(iskey)
        self.type = str(frame_type)

    def __str__(self):
        return 'V frame no %d, pts: %d, time: %f, type: %d' % (self.n, self.pts , self.time, self.type)

