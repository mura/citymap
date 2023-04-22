#!/usr/bin/env zsh
set -eux

SHAPE_FILE=./N03-20220101_GML/N03-22_220101.shp
rm -rf style
mkdir -p style/split
# Bounds生成用分割Shape (コード別)
npx mapshaper $SHAPE_FILE name='split' \
  -filter "N03_007 != null && N03_007 != \"\"" \
  -each "CODE=N03_007, delete N03_007" \
  -split CODE \
  -dissolve copy-fields=CODE \
  -o format=geojson encoding=utf8 bbox ./style/split/

# Bounds生成用分割Shape (政令指定都市・支庁別)
npx mapshaper $SHAPE_FILE name='split' \
  -each "if(N03_007 >= 27141 && N03_007 <= 27147){CODE_C=\"27140\"}else if(N03_007 >= 13381 && N03_007 <= 13382){CODE_C=\"13380\"}else if(N03_007 >= 14151 && N03_007 <= 14153){CODE_C=\"14150\"}else if(N03_007 >= 1481 && N03_007 <= 1487){CODE_C=\"01480\"}else if(N03_007 >= 13361 && N03_007 <= 13364){CODE_C=\"13360\"}else if(N03_007 >= 14131 && N03_007 <= 14137){CODE_C=\"14130\"}else if(N03_007 >= 1361 && N03_007 <= 1370){CODE_C=\"01360\"}else if(N03_007 >= 27102 && N03_007 <= 27128){CODE_C=\"27100\"}else if(N03_007 >= 15101 && N03_007 <= 15108){CODE_C=\"15100\"}else if(N03_007 >= 22131 && N03_007 <= 22137){CODE_C=\"22130\"}else if(N03_007 >= 13101 && N03_007 <= 13123){CODE_C=\"13100\"}else if(N03_007 >= 1101 && N03_007 <= 1110){CODE_C=\"01100\"}else if(N03_007 >= 1541 && N03_007 <= 1563){CODE_C=\"01540\"}else if(N03_007 >= 23101 && N03_007 <= 23116){CODE_C=\"23100\"}else if(N03_007 >= 1421 && N03_007 <= 1438){CODE_C=\"01420\"}else if(N03_007 >= 11101 && N03_007 <= 11110){CODE_C=\"11100\"}else if(N03_007 >= 33101 && N03_007 <= 33104){CODE_C=\"33100\"}else if(N03_007 >= 43101 && N03_007 <= 43105){CODE_C=\"43100\"}else if(N03_007 >= 40131 && N03_007 <= 40137){CODE_C=\"40130\"}else if(N03_007 >= 1301 && N03_007 <= 1304){CODE_C=\"01300\"}else if(N03_007 >= 13401 && N03_007 <= 13402){CODE_C=\"13400\"}else if(N03_007 >= 1601 && N03_007 <= 1610){CODE_C=\"01600\"}else if(N03_007 >= 1391 && N03_007 <= 1409){CODE_C=\"01390\"}else if(N03_007 >= 28101 && N03_007 <= 28111){CODE_C=\"28100\"}else if(N03_007 >= 1691 && N03_007 <= 1700){CODE_C=\"01690\"}else if(N03_007 >= 1571 && N03_007 <= 1586){CODE_C=\"01570\"}else if(N03_007 >= 4101 && N03_007 <= 4105){CODE_C=\"04100\"}else if(N03_007 >= 26101 && N03_007 <= 26111){CODE_C=\"26100\"}else if(N03_007 >= 1451 && N03_007 <= 1472){CODE_C=\"01450\"}else if(N03_007 >= 14101 && N03_007 <= 14118){CODE_C=\"14100\"}else if(N03_007 >= 1331 && N03_007 <= 1346){CODE_C=\"01330\"}else if(N03_007 >= 12101 && N03_007 <= 12106){CODE_C=\"12100\"}else if(N03_007 >= 34101 && N03_007 <= 34108){CODE_C=\"34100\"}else if(N03_007 >= 22101 && N03_007 <= 22103){CODE_C=\"22100\"}else if(N03_007 >= 1631 && N03_007 <= 1649){CODE_C=\"01630\"}else if(N03_007 >= 1511 && N03_007 <= 1519){CODE_C=\"01510\"}else if(N03_007 >= 40101 && N03_007 <= 40109){CODE_C=\"40100\"}" \
  -filter "CODE_C != null && CODE_C != \"\"" \
  -each "CODE=CODE_C" \
  -split CODE \
  -dissolve copy-fields=CODE \
  -o format=geojson encoding=utf8 bbox ./style/split/

# Bounds生成用分割Shape (都道府県別)
npx mapshaper $SHAPE_FILE name='split' \
  -each "if(N03_001==\"大分県\"){CODE=\"44\"}else if(N03_001==\"静岡県\"){CODE=\"22\"}else if(N03_001==\"宮崎県\"){CODE=\"45\"}else if(N03_001==\"北海道\"){CODE=\"01\"}else if(N03_001==\"愛知県\"){CODE=\"23\"}else if(N03_001==\"鹿児島県\"){CODE=\"46\"}else if(N03_001==\"青森県\"){CODE=\"02\"}else if(N03_001==\"三重県\"){CODE=\"24\"}else if(N03_001==\"沖縄県\"){CODE=\"47\"}else if(N03_001==\"岩手県\"){CODE=\"03\"}else if(N03_001==\"滋賀県\"){CODE=\"25\"}else if(N03_001==\"宮城県\"){CODE=\"04\"}else if(N03_001==\"京都府\"){CODE=\"26\"}else if(N03_001==\"秋田県\"){CODE=\"05\"}else if(N03_001==\"大阪府\"){CODE=\"27\"}else if(N03_001==\"山形県\"){CODE=\"06\"}else if(N03_001==\"兵庫県\"){CODE=\"28\"}else if(N03_001==\"福島県\"){CODE=\"07\"}else if(N03_001==\"奈良県\"){CODE=\"29\"}else if(N03_001==\"茨城県\"){CODE=\"08\"}else if(N03_001==\"栃木県\"){CODE=\"09\"}else if(N03_001==\"和歌山県\"){CODE=\"30\"}else if(N03_001==\"鳥取県\"){CODE=\"31\"}else if(N03_001==\"群馬県\"){CODE=\"10\"}else if(N03_001==\"島根県\"){CODE=\"32\"}else if(N03_001==\"埼玉県\"){CODE=\"11\"}else if(N03_001==\"岡山県\"){CODE=\"33\"}else if(N03_001==\"千葉県\"){CODE=\"12\"}else if(N03_001==\"広島県\"){CODE=\"34\"}else if(N03_001==\"山口県\"){CODE=\"35\"}else if(N03_001==\"東京都\"){CODE=\"13\"}else if(N03_001==\"徳島県\"){CODE=\"36\"}else if(N03_001==\"神奈川県\"){CODE=\"14\"}else if(N03_001==\"香川県\"){CODE=\"37\"}else if(N03_001==\"新潟県\"){CODE=\"15\"}else if(N03_001==\"愛媛県\"){CODE=\"38\"}else if(N03_001==\"富山県\"){CODE=\"16\"}else if(N03_001==\"高知県\"){CODE=\"39\"}else if(N03_001==\"石川県\"){CODE=\"17\"}else if(N03_001==\"福井県\"){CODE=\"18\"}else if(N03_001==\"山梨県\"){CODE=\"19\"}else if(N03_001==\"福岡県\"){CODE=\"40\"}else if(N03_001==\"佐賀県\"){CODE=\"41\"}else if(N03_001==\"長崎県\"){CODE=\"42\"}else if(N03_001==\"長野県\"){CODE=\"20\"}else if(N03_001==\"熊本県\"){CODE=\"43\"}else if(N03_001==\"岐阜県\"){CODE=\"21\"}" \
  -filter "CODE != null && CODE != \"\"" \
  -split CODE \
  -dissolve copy-fields=CODE \
  -o format=geojson encoding=utf8 bbox ./style/split/

# 不要データ(分割時のあまり)
if [[ -f ./style/split/split-.json ]]; then
  rm ./style/split/split-.json ./style/split/split-null.json
fi

# 市町村Shape
npx mapshaper $SHAPE_FILE \
  -each "CODE=N03_007, CODE_P=N03_007.substr(0, 2), delete N03_007" \
  -each "if(CODE >= 27141 && CODE <= 27147){CODE_C=\"27140\"}else if(CODE >= 13381 && CODE <= 13382){CODE_C=\"13380\"}else if(CODE >= 14151 && CODE <= 14153){CODE_C=\"14150\"}else if(CODE >= 1481 && CODE <= 1487){CODE_C=\"01480\"}else if(CODE >= 13361 && CODE <= 13364){CODE_C=\"13360\"}else if(CODE >= 14131 && CODE <= 14137){CODE_C=\"14130\"}else if(CODE >= 1361 && CODE <= 1370){CODE_C=\"01360\"}else if(CODE >= 27102 && CODE <= 27128){CODE_C=\"27100\"}else if(CODE >= 15101 && CODE <= 15108){CODE_C=\"15100\"}else if(CODE >= 22131 && CODE <= 22137){CODE_C=\"22130\"}else if(CODE >= 13101 && CODE <= 13123){CODE_C=\"13100\"}else if(CODE >= 1101 && CODE <= 1110){CODE_C=\"01100\"}else if(CODE >= 1541 && CODE <= 1563){CODE_C=\"01540\"}else if(CODE >= 23101 && CODE <= 23116){CODE_C=\"23100\"}else if(CODE >= 1421 && CODE <= 1438){CODE_C=\"01420\"}else if(CODE >= 11101 && CODE <= 11110){CODE_C=\"11100\"}else if(CODE >= 33101 && CODE <= 33104){CODE_C=\"33100\"}else if(CODE >= 43101 && CODE <= 43105){CODE_C=\"43100\"}else if(CODE >= 40131 && CODE <= 40137){CODE_C=\"40130\"}else if(CODE >= 1301 && CODE <= 1304){CODE_C=\"01300\"}else if(CODE >= 13401 && CODE <= 13402){CODE_C=\"13400\"}else if(CODE >= 1601 && CODE <= 1610){CODE_C=\"01600\"}else if(CODE >= 1391 && CODE <= 1409){CODE_C=\"01390\"}else if(CODE >= 28101 && CODE <= 28111){CODE_C=\"28100\"}else if(CODE >= 1691 && CODE <= 1700){CODE_C=\"01690\"}else if(CODE >= 1571 && CODE <= 1586){CODE_C=\"01570\"}else if(CODE >= 4101 && CODE <= 4105){CODE_C=\"04100\"}else if(CODE >= 26101 && CODE <= 26111){CODE_C=\"26100\"}else if(CODE >= 1451 && CODE <= 1472){CODE_C=\"01450\"}else if(CODE >= 14101 && CODE <= 14118){CODE_C=\"14100\"}else if(CODE >= 1331 && CODE <= 1346){CODE_C=\"01330\"}else if(CODE >= 12101 && CODE <= 12106){CODE_C=\"12100\"}else if(CODE >= 34101 && CODE <= 34108){CODE_C=\"34100\"}else if(CODE >= 22101 && CODE <= 22103){CODE_C=\"22100\"}else if(CODE >= 1631 && CODE <= 1649){CODE_C=\"01630\"}else if(CODE >= 1511 && CODE <= 1519){CODE_C=\"01510\"}else if(CODE >= 40101 && CODE <= 40109){CODE_C=\"40100\"}" \
  -o format=shapefile ./style/shape.shp

pushd style
  if [ -f citymap.zip ]; then
    rm citymap.zip
  fi
  7z a citymap.zip shape.*
popd
