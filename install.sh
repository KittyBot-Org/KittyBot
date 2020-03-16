#!/bin/bash

apt install jq
apt install curl
apt install wget

dir=$(dirname $(readlink -f $0))

rm /etc/systemd/system/kittybot.service
rm $dir/start.sh
rm $dir/KittyBot.jar

url=$(curl -sL https://api.github.com/repos/TopiSenpai/KittyBot/releases/latest | jq -r '.assets[].browser_download_url')

wget -O KittyBot.jar $url

echo "[Unit]
Description=KittyBot Service
After=mysqld.service

[Service]
Type=simple
User=$USER
ExecStart=$dir/start.sh

[Install]
WantedBy=multi-user.target" >> /etc/systemd/system/kittybot.service

echo "#!/bin/bash

java -jar $dir/KittyBot.jar" >> start.sh

chmod +x start.sh

systemctl daemon-reload
