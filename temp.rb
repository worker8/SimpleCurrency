require 'octokit'
require 'nokogiri'

puts "ARGV: #{ARGV}"
BOT_GITHUB_TOKEN = ARGV[0]
PR_NUMBER = ARGV[1]
repo_name = "worker8/SimpleCurrency"

file = File.open("./app/build/reports/lint-results.xml")

warning_body = <<~EOF
<details>
<summary> <strong>Warnings (XXwarning_numberXX) </strong> </summary>

### Warnings :warning:
| File | Explanation |
| ---- | ----------- |
EOF

error_body = <<~EOF
<details>
<summary> <strong>Errors (XXerror_numberXX) </strong> </summary>

### Errors :skull:
| File | Explanation |
| ---- | ----------- |
EOF

doc = Nokogiri::XML(file)
issues = doc.xpath("//issues").children.select { |x| x.attr("id") != nil }
#puts "issues: #{issues}"
warning_count = 0
error_count = 0

issues.map do |issue| 
  id = issue.attr("id")
  severity = issue.attr("severity")
  message = issue.attr("message")
  category = issue.attr("category")
  priority = issue.attr("priority")
  summary = issue.attr("summary")
  explanation = issue.attr("explanation")
  errorLine1 = issue.attr("errorLine1")
  errorLine2 = issue.attr("errorLine2")
  location = issue.children[1]
  file = location.attr("file").to_s.gsub(Dir.pwd + "/","")
  line = location.attr("line")
  column = location.attr("column")
  
  # ----- rendering ----
  if severity.casecmp? "warning"
    warning_count += 1
    warning_body += "| #{file}<br><details><summary>Line: #{line} Column: #{column}</summary>`#{errorLine1}`</details> | <details><summary>#{message}</summary> <br>#{explanation.gsub("\n"," ")}</details> \n"
  elsif severity.casecmp? "error"
    error_count += 1
    error_body += "| #{file}<br><details><summary>Line: #{line} Column: #{column}</summary>`#{errorLine1}`</details> | <details><summary>#{message}</summary> <br>#{explanation.gsub("\n"," ")}</details> \n"
  end
end

#puts warning_body
#puts error_body

warning_body.gsub!("XXwarning_numberXX", warning_count.to_s)
warning_body += "</details>"
error_body.gsub!("XXerror_numberXX", error_count.to_s)
error_body += "</details>"

if warning_count == 0 && error_count == 0
  body_string = "There is no warnings and errors by Android Lint! All good! :)"
elsif warning_count != 0 && error_count == 0
  body_string = warning_body
elsif warning_count -= 0 && error_count != 0
  body_string = error_body
elsif warning_count != 0 && error_count != 0
  body_string = error_body + "<br>" + warning_body
end
 
#puts body_string

# ----- sending to Github Pull Request --------
client = Octokit::Client.new(:access_token => BOT_GITHUB_TOKEN)
client.add_comment(repo_name, PR_NUMBER, body_string)
