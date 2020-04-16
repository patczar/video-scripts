# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# Implementation of the main video processing procedure.
from abc import ABC, abstractmethod

from video_scripts.commons import try_read_number
from video_scripts.ffmpeg import FFMPEG, FFInput, FFFilterChain, FFFilterGraph, FFFilter, FFOutput


TRIM_STRATEGY = 'TRIM_STRATEGY_ARGS'


def vid_process(script):
    scr = parse_script_file(script)

    builder = VidProcessBuilder()

    for step in scr.steps:
        print(step)
        if step.whoAreYou() == 'file':
            builder.accept_file(step)

    ffmpeg = builder.createFFMPEG()

    print(ffmpeg.getScript())


class VidProcessBuilder:
    def __init__(self):
        self.inputs = []
        self.input_nr = 0
        self.input_chains = []
        self.vlabels = []
        self.output = self.default_output()

    def default_output(self):
        return FFOutput('out.mp4')

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

    def createFFMPEG(self):
        filtergraph = FFFilterGraph()
        for chain in self.input_chains:
            filtergraph.addChain(chain)

        concat_chain = FFFilterChain(start_labels=self.vlabels, end_labels='vout',
                                     steps=FFFilter('concat', n=self.input_nr, v=1, a=0))
        filtergraph.addChain(concat_chain)
        self.output.maps.append('vout')
        return FFMPEG(inputs=self.inputs, outputs=self.output, filters=filtergraph)


class SomeSpec(ABC):
    @abstractmethod
    def whoAreYou(self):
        pass


class FileSpec(SomeSpec):
    def __init__(self, path, start=None, end=None):
        self.path = path
        self.start = start
        self.end = end

    def whoAreYou(self):
        return 'file'

    def __str__(self):
        return f'FILE {self.path} {self.start}:{self.end}'


class OptionSpec(SomeSpec):
    def __init__(self, what, params=None):
        self.what = what
        self.params = {} if params is None else params

    def addParam(self, key, value):
        self.params[key] = value

    def whoAreYou(self):
        return self.what

    def __str__(self):
        return f'-{self.what}' + ''.join(f' {name}={arg}' for name, arg in self.params.items())



class Script:
    def __init__(self):
        self.steps = []

    def addStep(self, step):
        self.steps.append(step)


specs = {'set-input-params',
         'set-output-params',
         'set-speed',
         'set-audio',
         'set-video',
        }

def parse_script_file(path):
    script = Script()
    with open(path, mode='r', encoding='utf-8') as file:
        for line in file:
            step = parse_line(line)
            if step is None: continue
            script.addStep(step)
    return script


# For the first attempt, I'm using very basic parsing techniques...

def parse_line(line):
    if line is None or len(line) == 0 or line[0] == '#':
        return None
    what, pos = find_word(line, 0)
    if what == 'file':
        path, pos = parse_arg(line, pos)
        params, pos = parse_params(line, pos)
        return FileSpec(path, params.get('s'), params.get('e'))
    elif what in specs:
        params, pos = parse_params(line, pos)
        return OptionSpec(what, params)


def skip(s:str, pos:int) -> int:
    for i in range(pos, len(s)):
        if not s[i].isspace():
            return i
    return len(s)


def parse_params(s:str, pos:int):
    result = {}
    pos = skip(s, pos)
    while pos < len(s):
        if s[pos] != '-':
            raise ValueError('- expected at this point')
        name, pos = find_word(s, pos+1)
        arg, pos = parse_arg(s, pos)
        result[name] = arg
        pos = skip(s, pos)
    return result, pos


def parse_arg(s:str, pos:int) -> (object, int):
    pos = skip(s, pos)
    if pos >= len(s):
        return None, pos
    contents = None
    end = pos
    if s[pos] == '"':
        end = s.find('"', pos+1)
        contents = s[pos+1:end]
        end += 1
    else:
        contents, end = find_word(s, pos)
    result = interpret_arg(contents)
    return result, end


def find_word(s:str, pos:int) -> (str, int):
    for i in range(pos, len(s)):
        if s[i].isspace():
            return s[pos:i], i
    else:
        return s[pos:], len(s)


def interpret_arg(s:str):
    if ',' in s:
        args = s.split(',')
        return [interpret_arg(arg) for arg in args]
    else:
        return try_read_number(s)
