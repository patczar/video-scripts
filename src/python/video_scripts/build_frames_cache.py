# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This program builds a cache of audio/video frames information.


from video_scripts.frames import make_frames_for_video_files
import sys

def main(args):
    video_files = args[1:]
    make_frames_for_video_files(video_files)
    
    
if __name__ == '__main__':
    main(sys.argv)
