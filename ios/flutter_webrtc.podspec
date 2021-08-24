#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'hi_webrtc'
  s.version          = '0.2.2'
  s.summary          = 'Flutter WebRTC plugin for iOS.'
  s.description      = <<-DESC
A new flutter plugin project.
                       DESC
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  s.dependency 'Libyuv', '1703'
  s.dependency 'GoogleWebRTC', '1.1.31999'
  s.ios.deployment_target = '10.0'
  s.static_framework = true
end

