#!/bin/bash -xe

DID=3400205
BASE=http://localhost:47800 # Depending on networking setup, might require ssh tunnel
DEVICE=api/v1/bacnet/devices/$DID
OUTDIR=readings
FBASE=https://lightbeam-c3076.firebaseio.com 
ROOT=building/mtv2637/device/$DID
OPTS="-X GET --header 'Accept: application/json'"
FCMD="firebase-import --force --database_url $FBASE"

rm -rf $OUTDIR
mkdir $OUTDIR

OIDS="5.4000004 0.2"

curl $OPTS "$BASE/$DEVICE" > $OUTDIR/device.json
$FCMD --path $ROOT/device --json $OUTDIR/device.json

curl $OPTS "$BASE/$DEVICE/objects?page=1&limit=100" > $OUTDIR/objects.json
$FCMD --path $ROOT/objects --json $OUTDIR/objects.json

for oid in $OIDS; do
  oid2=${oid//./_}
  curl $OPTS "$BASE/$DEVICE/objects/$oid" > $OUTDIR/object-$oid2.json
  $FCMD --path $ROOT/object/$oid2 --json $OUTDIR/object-$oid2.json
done
