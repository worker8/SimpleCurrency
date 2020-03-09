puts "----hello from Ruby----"
puts "args: #{ARGV}"

file = File.open("./SimpleCurrency/app/build/reports/lint-results.xml")
puts file.read
