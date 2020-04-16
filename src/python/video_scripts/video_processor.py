# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# Implementation of the main video processing procedure.
from abc import ABC, abstractmethod

from video_scripts.commons import try_read_number


def vid_process(script, out):
    scr = parse_script_file(script)

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


class OptionSpec(SomeSpec):
    def __init__(self, what, params=None):
        self.what = what
        self.params = {} if params is None else params

    def addParam(self, key, value):
        self.params[key] = value

    def whoAreYou(self):
        return self.what



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
            if step in None: continue
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
