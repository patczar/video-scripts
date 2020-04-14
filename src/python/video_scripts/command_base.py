# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module define base classes for all command-generating classes in the project.

from abc import ABC, abstractmethod, abstractproperty
from collections.abc import Iterable


class Scriptable(ABC):
    @abstractmethod
    def getScript(self):
        return ''

    @staticmethod
    def scriptFor(obj, sep=' '):
        if obj is None: return ''
        if isinstance(obj, str): return obj
        if isinstance(obj, Scriptable): return obj.getScript()
        if isinstance(obj, Iterable): return Scriptable.scriptForCollection(obj, sep)
        return str(obj)

    @staticmethod
    def scriptForCollection(collection, sep=' '):
        return sep.join(Scriptable.scriptFor(item) for item in collection)


class AbstractCommand(Scriptable):
    def run(self):
        pass # TODO

