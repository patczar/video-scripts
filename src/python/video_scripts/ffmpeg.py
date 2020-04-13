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


class FFFilters(Textible):
    pass



