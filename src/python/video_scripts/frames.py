# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module defines classes and functions to identify and search for individual frames of videos.

from subprocess import call
import re
import sys
from typing import List

pv = re.compile(r"\[Parsed_showinfo.*\]\s+n:\s*(\d+)\s+pts:\s*(\d+)\s+pts_time:\s*(\d+(?:\.\d+)?)\s+pos:\s*(\d+).+iskey:(\d)\s+type:(.)\s+checksum:([^\s]+).*")
pa = re.compile(r"\[Parsed_ashowinfo.*\]\s+n:\s*(\d+)\s+pts:\s*(\d+)\s+pts_time:\s*(\d+(?:\.\d+)?)\s+pos:\s*(\d+).+nb_samples:(\d+)\s+checksum:([^\s]+).*")


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
    
    def csv_fields(self):
        raise NotImplementedError('This is abstract method, should be implemented in subclasses')
    
    @staticmethod
    def of_csv_fields(fields:List[str]):
        '''
        Creates a Frame object based on the list of CSV fields, as exported by these classes.
        '''
        if len(fields) > 0:
            if fields[0] == 'A':
                return AudioFrame.of_csv_fields(fields)
            if fields[0] == 'V':
                return VideoFrame.of_csv_fields(fields)
        return None

    @staticmethod
    def of_showinfo_line(line:str):
        ma = pa.search(line)
        if(ma):
            return AudioFrame(ma[1], ma[2], ma[3], ma[4], ma[5], ma[6])
        mv = pv.search(line)
        if(mv):
            return VideoFrame(mv[1], mv[2], mv[3], mv[4], mv[5], mv[6], mv[7])
        return None


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
    
    def csv_fields(self):
        return ['A', self.n, self.pts , self.time, self.pos, self.samples, self.checksum]
    
    @staticmethod
    def of_csv_fields(fields:List[str]):
        return AudioFrame(*fields[1:])


class VideoFrame(Frame):
    '''
    An object of this class represents a video frame from a multimedia file.
    The meaning of this term and the details attached to it are taken from the ffmpeg tool.
    '''
    def __init__(self, n, pts, time, pos, iskey, frame_type, checksum=None):
        super().__init__(n, pts, time, pos, checksum)
        if type(iskey) == str:
            self.iskey = (iskey.strip() == '1')
        else:
            self.iskey = bool(iskey)
        self.type = str(frame_type)

    @property
    def iskey_num(self):
        return 1 if self.iskey else 0

    def __str__(self):
        return 'V frame no %d, pts: %d, time: %f, type: %d' % (self.n, self.pts , self.time, self.type)

    def csv_fields(self):
        return ['V', self.n, self.pts , self.time, self.pos, self.iskey_num, self.type, self.checksum]    
    
    @staticmethod
    def of_csv_fields(fields:List[str]):
        return VideoFrame(*fields[1:])


def run_showinfo(video_file_path:str, txt_out, do_audio:bool=True, do_video:bool=True) -> None:
    '''
    Runs ffmpeg program in showinfo mode, to collect information about inividual frames.
    
    :param video_file_path: the video file
    :param txt_out: output stream to which the ffmpeg output will be written; it ca be an opened (!) file
    :param do_audio: should I process audio frames
    :param do_video: should I process video frames
    '''
    filters = []
    if do_audio:
        filters.append('[a:0]ashowinfo')
    if do_video:
        filters.append('[v:0]showinfo')
    if filters:
        filters_str = ';'.join(filters)
        call(['ffmpeg', '-y', '-i',video_file_path,'-filter_complex',filters_str,'-f','null','/dev/null'], stderr=txt_out)


def make_showinfo_file(video_file_path:str, txt_file_path:str, do_audio:bool=True, do_video:bool=True) -> None:
    '''
    Runs ffmpeg program in showinfo mode, to collect information about inividual frames.
    The result is a text file in ffmpeg-dependent format.
    
    :param video_file_path:
    :param txt_file_path:
    :param do_audio:
    :param do_video:
    '''
    with open(txt_file_path, 'wb') as ffout:
        run_showinfo(video_file_path, ffout, do_audio, do_video)


