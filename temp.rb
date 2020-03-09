puts "----hello from Ruby----"
puts "args: #{ARGV}"

puts "Dir.pwd: #{Dir.pwd}"
file = File.open("./app/build/reports/lint-results.xml")
puts file.read
