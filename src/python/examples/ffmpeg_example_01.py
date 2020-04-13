from video_scripts.ffmpeg import FFMPEG

cmd1 = FFMPEG(inputs='in.avi', output='out.mp4')

print(cmd1.getText())
