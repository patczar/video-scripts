from video_scripts.ffmpeg import FFMPEG

cmd1 = FFMPEG(inputs=['in1.avi', 'in2.avi', 'in3.avi'], outputs='out.mp4')

print(cmd1.getScript())
