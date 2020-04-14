from video_scripts.ffmpeg import FFMPEG, FFFilterGraph, FFFilterChain, FFFilter

cmd1 = FFMPEG(inputs='in1.avi', outputs='out.mp4')

f1 = FFFilter('format', 'yuv420p')
f2 = FFFilter('scale')
f2.addParameter('width', 800)
f2.addParameter('height', 600)

f3 = FFFilter('eq', gamma=1.3, contrast=1.05, saturation=1.03)

chain1 = FFFilterChain()
chain1.addStep(f1)
chain1.addStep(f2)
chain1.addStep(f3)
chain1.addStep(FFFilter('select', "'between(n,100,200)'"))

filters = FFFilterGraph()
filters.addChain(chain1)

cmd1.filters =  filters

cmd1.filters.newChain('AA', 'BB')\
    .addStep(FFFilter('fade', t='out', start='40.0', d=2))\
    .addStep(FFFilter('eq', gamma=1.3, contrast=1.05, saturation=1.03))


print(cmd1.getScript())
