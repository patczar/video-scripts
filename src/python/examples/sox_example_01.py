from video_scripts.sox import SOX

cmd1 = SOX(inputs='in1.flac', output='out.mp3', effects='swap')

print(cmd1.getScript())

cmd2 = SOX(inputs='in2.flac', output='out.mp3', effects=['swap', 'deemph'])

print(cmd2.getScript())
