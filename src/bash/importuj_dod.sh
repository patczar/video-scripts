#!/bin/bash

if [ -z "$CARVIDREPO" ]
then
CARVIDREPO="$HOME/carvid"
fi

src="$1"
dest="$CARVIDREPO/raw/dod"

cyear=`date +%Y`
cmonth=`date +%m`


for f in ${src}/*
do
  fname=${f##*/}
    
  dir=`date -r "$f" +'%Y/%Y-%m-%d'`

  echo "$f -> ${dest}/${dir}/${fname,,}"
  if [ -f "${dest}/${dir}/${fname,,}" ]
  then echo "PLIK ISTNIEJE"
  else
    mkdir -p "${dest}/${dir}"
    mv "$f" "${dest}/${dir}/${fname,,}"
  fi
done
