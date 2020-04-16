# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# My main video processing program.

import sys

from video_scripts.video_processor import vid_process

def main(args):
    vid_process(script=args[1])


if __name__ == '__main__':
    main(sys.argv)

