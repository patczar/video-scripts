# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# Implementation of the main video processing procedure.

from .ffmpeg import *
from .input_script import *

TRIM_STRATEGY = 'TRIM_STRATEGY_ARGS'


def vid_process(script):
    scr = parse_script_file(script)

    builder = VidProcessBuilder()

    for step in scr.steps:
        print(step)
        builder.next_step(step)

    ffmpeg = builder.createFFMPEG()
    print(ffmpeg.getScript())


option_names_mappers = {
    'global': {
        'overwrite': 'y',
    },
}

class VidProcessBuilder:
    def __init__(self):
        self.global_options = {}
        self.inputs = []
        self.input_nr = 0
        self.input_chains = []
        self.vlabels = []
        self.output = self.default_output()

    def default_output(self):
        return FFOutput('out.mp4')

    def next_step(self, step):
        mapping = {
            'file': self.accept_file,
            'set-global': self.accept_global_options,
            'set-input': self.accept_input_settings,
            'set-output': self.accept_output_settings,
            'set-video': self.accept_video_settings,
            'set-audio': self.accept_audio_settings,
            'set-speed': self.accept_speed_settings,
        }

        what = step.whoAreYou()
        case(what, mapping, step)

    def accept_file(self, file_step):
        file_spec = FFInput(file_step.path)
        if TRIM_STRATEGY == 'TRIM_STRATEGY_ARGS':
            if file_step.start:
                file_spec.addOption('ss', file_step.start)
            if file_step.end:
                file_spec.addOption('t', file_step.end)

        self.inputs.append(file_spec)
        lbl = f'v{self.input_nr}'
        self.input_chains.append(FFFilterChain(f'{self.input_nr}:0', lbl))
        self.vlabels.append(lbl)
        self.input_nr += 1

    def accept_global_options(self, option_spec:OptionSpec):
        for option, value in option_spec.params.items():
            if option in option_names_mappers['global']:
                option = option_names_mappers['global'][option]
                self.global_options[option] = value

    def accept_input_settings(self, option_spec):
        pass

    def accept_output_settings(self, option_spec):
        pass

    def accept_video_settings(self, option_spec):
        pass

    def accept_audio_settings(self, option_spec):
        pass

    def accept_speed_settings(self, option_spec):
        pass

    def createFFMPEG(self):
        filtergraph = FFFilterGraph()
        for chain in self.input_chains:
            filtergraph.addChain(chain)

        concat_chain = FFFilterChain(start_labels=self.vlabels, end_labels='vout',
                                     steps=FFFilter('concat', n=self.input_nr, v=1, a=0))
        filtergraph.addChain(concat_chain)
        self.output.maps.append('vout')
        return FFMPEG(inputs=self.inputs, outputs=self.output, global_options=self.global_options, filters=filtergraph)

