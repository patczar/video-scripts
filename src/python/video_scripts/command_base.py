# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module define base classes for all command-generating classes in the project.

from abc import ABC, abstractmethod, abstractproperty
from collections.abc import Iterable, Sequence
import typing as t
from subprocess import call

from video_scripts.commons import default_list


# Interfaces for commands and their fragments

class ACommandFragment(ABC):
    @abstractmethod
    def cmd_elements(self) -> t.Sequence[str]:
        return []

    def cmd_string(self) -> str:
        return ' '.join(self.cmd_elements())

    def __str__(self) -> str:
        return self.cmd_string()


class AScript(ABC):
    @abstractmethod
    def cmds(self) -> t.Sequence:
        return []


class ACommand(ACommandFragment, AScript):
    @abstractmethod
    def cmd(self) -> str:
        return ''

    @abstractmethod
    def options(self) -> t.Sequence[str]:
        return []

    def cmd_elements(self) -> t.Sequence[str]:
        return [self.cmd()] + list(self.options())

    def run(self, **run_options) -> t.NoReturn:
        call(self.cmd_elements())

    def cmds(self):
        return [self]


# Default implementations based on lists

class DCommandFragment(ACommandFragment):
    def __init__(self, options: t.Sequence[str] = ()):
        self._options = default_list(options)

    def cmd_elements(self) -> t.List[str]:
        return self._options

    def append(self, *next_elements):
        self._options.extend(cmd_elements_for(*next_elements))

    @staticmethod
    def of(*args):
        result = DCommandFragment()
        result.append(*args)
        return result


class DCommand(DCommandFragment, ACommand):
    def __init__(self, cmd: str, options: t.Sequence[str] = ()):
        super().__init__(options)
        self._cmd = cmd

    def cmd(self) -> str:
        return self._cmd

    def options(self) -> t.Sequence[str]:
        return self._options

    def cmd_elements(self) -> t.List[str]:
        return [self._cmd] + self._options

    def append(self, *next_elements):
        self._options.extend(cmd_elements_for(*next_elements))

    @staticmethod
    def of(cmd: str, *args):
        result = DCommand(cmd)
        result.append(*args)
        return result


# Utility functions

def cmd_elements_for(*args) -> t.Sequence[str]:

    def cmd_elements_for_one(arg):
        if arg is None:
            return []
        if isinstance(arg, ACommandFragment):
            return arg.cmd_elements()
        if isinstance(arg, Iterable) and not isinstance(arg, str):
            return cmd_elements_for_collection(arg)
        if isinstance(arg, str):
            return [arg]
        return [str(arg)]

    def cmd_elements_for_collection(col):
        result = []
        for arg in col:
            result.extend(cmd_elements_for_one(arg))
        return result

    return cmd_elements_for_collection(args)
