require 'json'
require 'fileutils'

class GenCityCode

  def initialize
    @data      = []
    @data_info = {}
  end

  def read(path)
    Dir.glob(path).sort.each do |path|
      str      = File.open(path).read(nil, '')
      json     = JSON.parse(str)
      bounds   = json['bbox']
      features = json['features']
      features.each do |feature|
        # 名称抽出
        unless feature['properties']['CODE'].nil?
          code = feature['properties']['CODE']
          @data << {:code => code, :bounds => bounds}
          next
        end
      end
    end
  end

  def make
    File.open("./bounds.json", 'w').write(JSON.generate(@data))
  end

end

city = GenCityCode.new
city.read("./style/split/split-*.json")

city.make()
