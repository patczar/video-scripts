# (c) Patryk Czarnik
# Distributed under MIT License. See LICENCE file in the root directory for details.

# This module contains common helper functions.

from collections.abc import Iterable, Sequence, Mapping


def ident(x):
    '''The identity function.'''
    return x

def default_string(value):
    '''
        Produces a string value based on the given value.
        To be used during initialization of object attributes etc.
    '''
    if value is None: return ''
    if isinstance(value, str): return value
    return str(value)


def default_list(value, mapper=ident):
    '''
        Produces a list based on the given value.
        To be used during initialization of object attributes etc.

        If the value is None, than an empty list is returned.
        If the value is an iterable, but not a string, then a new list of the same elements is returned.
        If the value is a string or a non-iterable object, then a singleton list with that element is returned.

        mapper is an optional function that will be applied to each element of the list being returned.
    '''
    if value is None: return []
    if isinstance(value, Iterable) and not isinstance(value, str):
        return [mapper(x) for x in value]
    return [mapper(value)]


def default_dict(value, mapper=lambda x: (x, None), mapper2=lambda x, y: (x, y)):
    def iterpret_item(item):
        if item is None: return None
        if isinstance(value, Sequence) and not isinstance(value, str) and len(value) == 1:
            return iterpret_item(item[0])
        if isinstance(value, Sequence) and not isinstance(value, str) and len(value) == 2:
            return mapper2(value[0], value[1])
        if isinstance(value, Sequence) and not isinstance(value, str) and len(value) > 2:
            return mapper2(value[0], value[1:])
        (k, v) = mapper(item)
        return (k, v)

    if value is None: return {}
    if isinstance(value, Mapping) and hasattr(value, 'items'):
        return dict(mapper2(k, v) for k, v in value.items())
    # special treatment of a single tuple of size 2: as key->value
    if isinstance(value, tuple) and len(value) == 2:
        k, v = mapper2(*value)
        return {k: v}
    if isinstance(value, Iterable) and not isinstance(value, str):
        return dict(iterpret_item(item) for item in value)
    return iterpret_item(value)


def try_read_number(s: str):
    if s is None:
        return None
    try:
        return int(s.strip())
    except:
        try:
            return float(s.strip())
        except:
            return s


def case(key, mapping, *args, **kwargs):
    choice = mapping.get(key)
    if callable(choice):
        return choice(*args, **kwargs)
    else:
        return choice
