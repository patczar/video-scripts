from video_scripts.sox import SOX, SOXEffect

cmd1 = SOX(inputs=['in1.flac', 'in2.flac'], output='out.mp3')

print(cmd1.getScript())

cmd1.addEffect(SOXEffect('vol', 2.5))
cmd1.addEffect(SOXEffect('trim', 7.0, 12.25))
cmd1.addEffect('swap')
cmd1.addEffect(('pad', 1, 2))

print(cmd1.getScript())
