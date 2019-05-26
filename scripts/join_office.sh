#!/usr/bin/env zsh
PREFS=(北海道 青森県 岩手県 宮城県 秋田県 山形県 福島県 茨城県 栃木県 群馬県 埼玉県
    千葉県 東京都 神奈川県 新潟県 富山県 石川県 福井県 山梨県 長野県 岐阜県 静岡県
    愛知県 三重県 滋賀県 京都府 大阪府 兵庫県 奈良県 和歌山県 鳥取県 島根県 岡山県
    広島県 山口県 徳島県 香川県 愛媛県 高知県 福岡県 佐賀県 長崎県 熊本県 大分県
    宮崎県 鹿児島県 沖縄県)

rm -rf style
mkdir -p style/split
# Bounds生成用分割Shape
mapshaper ./N03-180101_GML/N03-18_180101.shp name='split' \
  -each "CODE=N03_007, CODE_P=N03_007.substr(0, 2), delete N03_007" \
  -each "if(CODE >= 40101 && CODE <= 40109){CODE_C=\"40100\"}else if(CODE >= 27141 && CODE <= 27147){CODE_C=\"27140\"}else if(CODE >= 14151 && CODE <= 14153){CODE_C=\"14150\"}else if(CODE >= 28101 && CODE <= 28111){CODE_C=\"28100\"}else if(CODE >= 14131 && CODE <= 14137){CODE_C=\"14130\"}else if(CODE >= 27102 && CODE <= 27128){CODE_C=\"27100\"}else if(CODE >= 26101 && CODE <= 26111){CODE_C=\"26100\"}else if(CODE >= 15101 && CODE <= 15108){CODE_C=\"15100\"}else if(CODE >= 4101 && CODE <= 4105){CODE_C=\"04100\"}else if(CODE >= 22131 && CODE <= 22137){CODE_C=\"22130\"}else if(CODE >= 14101 && CODE <= 14118){CODE_C=\"14100\"}else if(CODE >= 13101 && CODE <= 13123){CODE_C=\"13100\"}else if(CODE >= 34101 && CODE <= 34108){CODE_C=\"34100\"}else if(CODE >= 23101 && CODE <= 23116){CODE_C=\"23100\"}else if(CODE >= 12101 && CODE <= 12106){CODE_C=\"12100\"}else if(CODE >= 1101 && CODE <= 1110){CODE_C=\"01100\"}else if(CODE >= 33101 && CODE <= 33104){CODE_C=\"33100\"}else if(CODE >= 22101 && CODE <= 22103){CODE_C=\"22100\"}else if(CODE >= 11101 && CODE <= 11110){CODE_C=\"11100\"}else if(CODE >= 43101 && CODE <= 43105){CODE_C=\"43100\"}else if(CODE >= 40131 && CODE <= 40137){CODE_C=\"40130\"}" \
  -split CODE \
  -dissolve copy-fields=N03_001,N03_002,N03_003,N03_004,CODE,CODE_C,CODE_P \
  -o format=geojson encoding=utf8 bbox ./style/split/

mapshaper ./N03-180101_GML/N03-18_180101.shp name='split' \
  -each "if(N03_007 >= 40101 && N03_007 <= 40109){CODE=\"40100\"}else if(N03_007 >= 27141 && N03_007 <= 27147){CODE=\"27140\"}else if(N03_007 >= 14151 && N03_007 <= 14153){CODE=\"14150\"}else if(N03_007 >= 28101 && N03_007 <= 28111){CODE=\"28100\"}else if(N03_007 >= 14131 && N03_007 <= 14137){CODE=\"14130\"}else if(N03_007 >= 27102 && N03_007 <= 27128){CODE=\"27100\"}else if(N03_007 >= 26101 && N03_007 <= 26111){CODE=\"26100\"}else if(N03_007 >= 15101 && N03_007 <= 15108){CODE=\"15100\"}else if(N03_007 >= 4101 && N03_007 <= 4105){CODE=\"04100\"}else if(N03_007 >= 22131 && N03_007 <= 22137){CODE=\"22130\"}else if(N03_007 >= 14101 && N03_007 <= 14118){CODE=\"14100\"}else if(N03_007 >= 13101 && N03_007 <= 13123){CODE=\"13100\"}else if(N03_007 >= 34101 && N03_007 <= 34108){CODE=\"34100\"}else if(N03_007 >= 23101 && N03_007 <= 23116){CODE=\"23100\"}else if(N03_007 >= 12101 && N03_007 <= 12106){CODE=\"12100\"}else if(N03_007 >= 1101 && N03_007 <= 1110){CODE=\"01100\"}else if(N03_007 >= 33101 && N03_007 <= 33104){CODE=\"33100\"}else if(N03_007 >= 22101 && N03_007 <= 22103){CODE=\"22100\"}else if(N03_007 >= 11101 && N03_007 <= 11110){CODE=\"11100\"}else if(N03_007 >= 43101 && N03_007 <= 43105){CODE=\"43100\"}else if(N03_007 >= 40131 && N03_007 <= 40137){CODE=\"40130\"}" \
  -each "CODE_P=N03_007.substr(0, 2), delete N03_007, CODE_C=\"\"" \
  -split CODE \
  -dissolve copy-fields=N03_001,N03_002,N03_003,N03_004,CODE,CODE_C,CODE_P \
  -o format=geojson encoding=utf8 bbox ./style/split/

for N in {01..47}; do
  mapshaper -i encoding=utf8 ./style/split/split-${N}*.json combine-files \
    -each "CODE=CODE_P, CODE_P=\"\", N03_002=\"\", N03_003=\"\", N03_004=\"\", CODE_C=\"\"" \
    -merge-layers \
    -dissolve copy-fields=N03_001,N03_002,N03_003,N03_004,CODE,CODE_C,CODE_P \
    -o format=geojson encoding=utf8 bbox ./style/split/split-${N}.json
done

# 不要データ(分割時のあまり)
rm ./style/split/split-.json ./style/split/split-null.json

# 市町村役場Point
mapshaper -i encoding=sjis ./office/P34-14_{01..47}.shp combine-files \
  -merge-layers \
  -each "CODE=P34_001, CODE_P=P34_001.substr(0, 2), delete P34_001" \
  -each "if(CODE >= 40101 && CODE <= 40109){CODE_C=\"40100\"}else if(CODE >= 27141 && CODE <= 27147){CODE_C=\"27140\"}else if(CODE >= 14151 && CODE <= 14153){CODE_C=\"14150\"}else if(CODE >= 28101 && CODE <= 28111){CODE_C=\"28100\"}else if(CODE >= 14131 && CODE <= 14137){CODE_C=\"14130\"}else if(CODE >= 27102 && CODE <= 27128){CODE_C=\"27100\"}else if(CODE >= 26101 && CODE <= 26111){CODE_C=\"26100\"}else if(CODE >= 15101 && CODE <= 15108){CODE_C=\"15100\"}else if(CODE >= 4101 && CODE <= 4105){CODE_C=\"04100\"}else if(CODE >= 22131 && CODE <= 22137){CODE_C=\"22130\"}else if(CODE >= 14101 && CODE <= 14118){CODE_C=\"14100\"}else if(CODE >= 13101 && CODE <= 13123){CODE_C=\"13100\"}else if(CODE >= 34101 && CODE <= 34108){CODE_C=\"34100\"}else if(CODE >= 23101 && CODE <= 23116){CODE_C=\"23100\"}else if(CODE >= 12101 && CODE <= 12106){CODE_C=\"12100\"}else if(CODE >= 1101 && CODE <= 1110){CODE_C=\"01100\"}else if(CODE >= 33101 && CODE <= 33104){CODE_C=\"33100\"}else if(CODE >= 22101 && CODE <= 22103){CODE_C=\"22100\"}else if(CODE >= 11101 && CODE <= 11110){CODE_C=\"11100\"}else if(CODE >= 43101 && CODE <= 43105){CODE_C=\"43100\"}else if(CODE >= 40131 && CODE <= 40137){CODE_C=\"40130\"}" \
  -o encoding=utf8 format=geojson ./style/office.json

# 市町村Shape
mapshaper ./N03-180101_GML/N03-18_180101.shp \
  -each "CODE=N03_007, CODE_P=N03_007.substr(0, 2), delete N03_007" \
  -each "if(CODE >= 40101 && CODE <= 40109){CODE_C=\"40100\"}else if(CODE >= 27141 && CODE <= 27147){CODE_C=\"27140\"}else if(CODE >= 14151 && CODE <= 14153){CODE_C=\"14150\"}else if(CODE >= 28101 && CODE <= 28111){CODE_C=\"28100\"}else if(CODE >= 14131 && CODE <= 14137){CODE_C=\"14130\"}else if(CODE >= 27102 && CODE <= 27128){CODE_C=\"27100\"}else if(CODE >= 26101 && CODE <= 26111){CODE_C=\"26100\"}else if(CODE >= 15101 && CODE <= 15108){CODE_C=\"15100\"}else if(CODE >= 4101 && CODE <= 4105){CODE_C=\"04100\"}else if(CODE >= 22131 && CODE <= 22137){CODE_C=\"22130\"}else if(CODE >= 14101 && CODE <= 14118){CODE_C=\"14100\"}else if(CODE >= 13101 && CODE <= 13123){CODE_C=\"13100\"}else if(CODE >= 34101 && CODE <= 34108){CODE_C=\"34100\"}else if(CODE >= 23101 && CODE <= 23116){CODE_C=\"23100\"}else if(CODE >= 12101 && CODE <= 12106){CODE_C=\"12100\"}else if(CODE >= 1101 && CODE <= 1110){CODE_C=\"01100\"}else if(CODE >= 33101 && CODE <= 33104){CODE_C=\"33100\"}else if(CODE >= 22101 && CODE <= 22103){CODE_C=\"22100\"}else if(CODE >= 11101 && CODE <= 11110){CODE_C=\"11100\"}else if(CODE >= 43101 && CODE <= 43105){CODE_C=\"43100\"}else if(CODE >= 40131 && CODE <= 40137){CODE_C=\"40130\"}" \
  -o format=shapefile ./style/shape.shp

popd style
  if [ -f citymap.zip ]; then
    rm citymap.zip
  fi
  7z a citymap.zip shape.*
pushd