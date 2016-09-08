#!/bin/bash

APP_PATH=/opt/usr/apps/nx-remote-controller-mod
TOOLS_PATH=$APP_PATH/tools
CHROOT="chroot $TOOLS_PATH"
YAD="$CHROOT yad"

confirm_dialog() {
    $CHROOT yad --text="<big>\n Do you really want to uninstall?\n</big>" --button=gtk-no:1 \
                --button=gtk-yes:0 --buttons-layout=spread --center --width=650
}

if confirm_dialog; then
    $APP_PATH/off_nx-remote-controller-daemon.sh
    rm -f /opt/usr/nx-on-wake/auto/nx-remote-controller-daemon.sh
    rm -rf $APP_PATH
    sync; sync; sync
fi
