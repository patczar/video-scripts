# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module helps to create ffmpeg commands.

from .command_base import *
from collections.abc import Collection

def default_string(value):
    return '' if value is None else str(value)

def default_list(value):
    if isinstance(value, list): return value
    if value is None: return []
    if isinstance(value, str): return [value]
    if isinstance(value, Collection): return list(value)
    return [value]


class FFMPEG(AbstractCommand):
    def __init__(self, inputs=None, output=None, filters=None, maps=None, codecs=None):
        super().__init__()
        self.inputs = default_list(inputs)
        self.output = default_string(output)
        self.filters = default_list(filters)
        self.codecs = default_list(codecs)


    def getText(self):
        return f'ffmpeg {Textible.getTextForCollection(self.inputs)} {Textible.getTextFor(self.filters)} {Textible.getTextForCollection(self.codecs)} {self.output}'


class FFFilterGraph(Textible):
    def __init__(self):
        self.chains = []

    def getText(self):
        if len(self.chains) == 0:
            return ''
        else:
            return f'-filter_complex "{Textible.getTextForCollection(self.chains, ";")}"'

    def addChain(self, chain):
        if not chain.parent:
            chain.parent = self
        self.chains.append(chain)
        return self

    def newChain(self, start_label=None, end_label=None):
        chain = FFFilterChain(self, start_label, end_label)
        self.chains.append(chain)
        return chain


class FFFilterChain(Textible):
    def __init__(self, parent=None, start_label=None, end_label=None):
        self.parent = parent
        self.start_label = start_label
        self.end_label = end_label
        self.steps = []

    def getText(self):
        result = ''
        if self.start_label:
            result += f'[{self.start_label}]'
        result += Textible.getTextForCollection(self.steps, ',')
        if self.end_label:
            result += f'[{self.end_label}]'
        return result

    def finito(self):
        return self.parent

    def addStep(self, step):
        self.steps.append(step)
        return self


class FFFilter(Textible):
    def __init__(self, name, *args, **kwargs):
        self.name = name
        self.parameters = []
        for arg in args:
            self.addParameter('', arg)
        for key, arg in kwargs.items():
            self.addParameter(key, arg)

    def getText(self):
        result = self.name
        if len(self.parameters) > 0:
            result += '='
        for i, (par_name, par_value) in enumerate(self.parameters):
            if i > 0:
                result += ':'
            if par_name:
                result += par_name + '='
            result += FFFilter.escape_parameter_value(par_value)

        return result

    def addParameter(self, name='', value=''):
        self.parameters.append((name, value))
        return self

    ESCAPED_CHARS = {',', ':', ';', '\\'}
    @staticmethod
    def escape_parameter_value(value):
        if(not isinstance(value, str)):
            return str(value)
        if set(value) & FFFilter.ESCAPED_CHARS:
            return ''.join('\\'+ch if ch in FFFilter.ESCAPED_CHARS else ch for ch in value)
        return value

