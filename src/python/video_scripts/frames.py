# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module defines classes and functions to identify and search for individual frames of videos.

from subprocess import call
import re
import sys
from typing import List

from .settings import CSV_SEPARATOR, DEFAULT_FRAME_DIR, DEFAULT_FRAME_CSV_SUFFIX, DEFAULT_AFRAME_CSV_SUFFIX, DEFAULT_VFRAME_CSV_SUFFIX, DEFAULT_FRAME_TXT_SUFFIX
import os


# Patterns for showinfo files
pv = re.compile(r"\[Parsed_showinfo.*\]\s+n:\s*(\d+)\s+pts:\s*(\d+)\s+pts_time:\s*(\d+(?:\.\d+)?)\s+pos:\s*(\d+)\s+fmt:\s*([^\s]+)\s+sar:\s*([^\s]+)\s+s:\s*([^\s]+)\s+i:\s*([^\s]+)\s+iskey:(\d)\s+type:(.)\s+checksum:([^\s]+)\s+plane_checksum:\s*\[([^\]]+)\]\s+mean:\s*\[([^\]]+)?\]\s+stdev:\s*\[([^\]]+)?\].*")
pa = re.compile(r"\[Parsed_ashowinfo.*\]\s+n:\s*(\d+)\s+pts:\s*(\d+)\s+pts_time:\s*(\d+(?:\.\d+)?)\s+pos:\s*(\d+)\s+fmt:\s*([^\s]+)\s+channels:\s*(\d+)\s+chlayout:\s*([^\s]+)\s+rate:\s*(\d+)\s+nb_samples:\s*(\d+)\s+checksum:\s*([^\s]+)\s+plane_checksums:\s*\[([^\]]+)\].*")


class Frame:
    '''Abstract class representing a frame in a multimedia file.'''
    def __init__(self, n, pts, time, pos, checksum=None, plane_checksums=[]):
        self.n = int(n)
        self.pts = int(pts)
        self.time = float(time)
        self.pos = int(pos)
        self.checksum = checksum
        self.plane_checksums = Frame.prepare_plane_checksums(plane_checksums)

    def __str__(self):
        return 'frame no %d, pts: %d, time: %f' % (self.n, self.pts , self.time)
    
    @property
    def kind(self):
        return 'F'
    
    @property
    def plane_checksums_str(self):
        return ' '.join(self.plane_checksums)

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
            return AudioFrame(ma[1], ma[2], ma[3], ma[4], ma[5], ma[6], ma[7], ma[8], ma[9], ma[10], ma[11])
        mv = pv.search(line)
        if(mv):
            return VideoFrame(mv[1], mv[2], mv[3], mv[4], mv[5], mv[6], mv[7], mv[8], mv[9], mv[10], mv[11], mv[12], mv[13], mv[14])
        return None

    @staticmethod
    def prepare_plane_checksums(plane_checksums):
        if type(plane_checksums) == str:
            return [chunk for chunk in plane_checksums.split(' ') if chunk]
        else:
            return plane_checksums

    @staticmethod
    def prepare_plane_values(values_str):
        result = []
        for chunk in values_str.split(' '):
            try:
                result.append(int(chunk))
            except:
                try:
                    result.append(float(chunk))
                except:
                    pass
        return result


class AudioFrame(Frame):
    '''
    An object of this class represents an audio frame from a multimedia file.
    The meaning of this term and the details attached to it are taken from the ffmpeg tool.
    '''
    def __init__(self, n, pts, time, pos, fmt, channels, chlayout, rate, samples, checksum=None, plane_checksums=[]):
        super().__init__(n, pts, time, pos, checksum, plane_checksums)
        self.fmt = fmt
        self.channels = int(channels)
        self.chlayout = chlayout
        self.rate = int(rate)
        self.samples = int(samples)

    def __str__(self):
        return 'A frame no %d, pts: %d, time: %f, samples: %d' % (self.n, self.pts, self.time, self.samples)
    
    @property
    def kind(self):
        return 'A'
    
    def csv_fields(self):
        return ['A', self.n, self.pts , self.time, self.pos, self.fmt, self.channels, self.chlayout, self.rate, self.samples, self.checksum, self.plane_checksums_str]
    
    @staticmethod
    def of_csv_fields(fields:List[str]):
        return AudioFrame(*fields[1:])


class VideoFrame(Frame):
    '''
    An object of this class represents a video frame from a multimedia file.
    The details attached to it are taken from the ffmpeg tool.
    '''
    def __init__(self, n, pts, time, pos, fmt, sar, size, i, iskey, frame_type, checksum=None, plane_checksums=[], mean='', stdev=''):
        super().__init__(n, pts, time, pos, checksum, plane_checksums)
        self.fmt = fmt
        self.sar = sar
        self.size = size
        self.i = i
        if type(iskey) == str:
            self.iskey = (iskey.strip() == '1')
        else:
            self.iskey = bool(iskey)
        self.frame_type = str(frame_type)
        self.mean = Frame.prepare_plane_values(mean)
        self.stdev = Frame.prepare_plane_values(stdev)

    @property
    def iskey_num(self):
        return 1 if self.iskey else 0

    @property
    def kind(self):
        return 'V'
    
    @property
    def mean_str(self):
        return ' '.join((str(x) for x in self.mean))

    @property
    def stdev_str(self):
        return ' '.join((str(x) for x in self.stdev))

    def __str__(self):
        return 'V frame no %d, pts: %d, time: %f, type: %s' % (self.n, self.pts , self.time, self.frame_type)

    def csv_fields(self):
        return ['V', self.n, self.pts , self.time, self.pos, self.fmt, self.sar, self.size, self.i, self.iskey_num, self.frame_type, self.checksum, self.plane_checksums_str, self.mean_str, self.stdev_str]
    
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
    
    :param video_file_path: the video file
    :param txt_file_path: path to the output file; the file will be overwritten
    :param do_audio: should I process audio frames
    :param do_video: should I process video frames
    '''
    with open(txt_file_path, 'wb') as ffout:
        run_showinfo(video_file_path, ffout, do_audio, do_video)


def make_csv_files(txt_file_path:str, common_csv_file_path:str=None, audio_csv_file_path:str=None, video_csv_file_path:str=None):
    '''
    Creates CSV file(s) containing list of audio/video frames and their details, based on txt file previously generated using make_showinfo_file.
    
    :param txt_file_path:
    :param common_csv_file_path:
    :param audio_csv_file_path:
    :param video_csv_file_path:
    '''
    
    in_txt = out_common = out_audio = out_video = None
    audio_goals = []
    video_goals = []
    try:
        in_txt = open(txt_file_path, 'r')
        if common_csv_file_path:
            out_common = open(common_csv_file_path, 'w')
            audio_goals.append(out_common)
            video_goals.append(out_common)
        if audio_csv_file_path:
            out_audio = open(audio_csv_file_path, 'w')
            audio_goals.append(out_audio)
        if video_csv_file_path:
            out_video = open(video_csv_file_path, 'w')
            video_goals.append(out_video)
        for line in in_txt:
            frame = Frame.of_showinfo_line(line)
            if frame is not None:
                fields = frame.csv_fields()
                if frame.kind == 'A':
                    for f in audio_goals:
                        print(*fields, sep=CSV_SEPARATOR, file=f)
                if frame.kind == 'V':
                    for f in video_goals:
                        print(*fields, sep=CSV_SEPARATOR, file=f)
    finally:
        if out_video is not None:
            out_video.close()
        if out_audio is not None:
            out_audio.close()
        if out_common is not None:
            out_common.close()


def make_frames_for_video_file(video_file_path:str, txt_file_path:str,
                               do_audio=True, do_video=True,
                               common_csv_file_path:str=None, audio_csv_file_path:str=None, video_csv_file_path:str=None):
    make_showinfo_file(video_file_path, txt_file_path, do_audio, do_video)
    make_csv_files(txt_file_path, common_csv_file_path, audio_csv_file_path, video_csv_file_path)


def make_frames_for_video_files(video_files:List[str], do_audio=True, do_video=True,
                                directory=DEFAULT_FRAME_DIR,
                                txt_suffix=DEFAULT_FRAME_TXT_SUFFIX,
                                common_csv_suffix=DEFAULT_FRAME_CSV_SUFFIX,
                                audio_csv_suffix=DEFAULT_AFRAME_CSV_SUFFIX,
                                video_csv_suffix=DEFAULT_VFRAME_CSV_SUFFIX):
    os.makedirs(directory, exist_ok=True)
    for video_file in video_files:
        txt_file_path = f'{directory}/{video_file}{txt_suffix}'
        common_csv_file_path = f'{directory}/{video_file}{common_csv_suffix}'
        audio_csv_file_path = f'{directory}/{video_file}{audio_csv_suffix}'
        video_csv_file_path = f'{directory}/{video_file}{video_csv_suffix}'

        make_frames_for_video_file(video_file, txt_file_path, do_audio, do_video, common_csv_file_path, audio_csv_file_path, video_csv_file_path)
        

