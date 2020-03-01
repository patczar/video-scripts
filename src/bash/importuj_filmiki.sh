#!/bin/bash

if [ -z "$CARVIDREPO" ]
then
CARVIDREPO="$HOME/carvid"
fi

src="$1"
dest_mp4="$CARVIDREPO/raw/ferguson"
dest_mov="$CARVIDREPO/raw/a7810"

cyear=`date +%Y`
cmonth=`date +%m`

if [ -n "$2" ]
then
year="$2"
else
year="$cyear"
fi

for f in ${src}/*
do
  fname=${f##*/}
  name=${fname%.*}
  ext=${fname#*.}
  
  
  case "$ext" in
  MOV|mov)
    dest="$dest_mov"
    ext=mov
  ;;
  MP4|mp4)
    dest="$dest_mp4"
    ext=mp4
  ;;
  *)
    dest=""
  esac
  
  fmonth=${name:0:2}
  fday=${name:2:2}
  
  dir="${year}/${year}-${fmonth}-${fday}"

  if [ -n "$dest" ]
  then
  echo "$f -> ${dest}/${dir}/${name}.${ext}"
  mkdir -p "${dest}/${dir}"
  mv "$f" "${dest}/${dir}/${name}.${ext}"
  fi
done

