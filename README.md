1. One can get the score for user's played frames.
https://en.wikipedia.org/wiki/Ten-pin_bowling#Pins_and_scoring

2. API Documentation
http://localhost:8080/bowling-api/swagger-ui/index.html

To test a perfect game (gaining maximum score) post subsequently 11 times
http://localhost:8080/bowling-api/bowling/frames/1

with the following JSON request body:
{"frameIdx":1,
"knockedPins": [10,0]
}
where frameIdx is incremented by 1 each time up to 11 for the last bonus frame.

To run in container via terminal:
1. go to the root directory of the project. 
2. execute command : docker-compose up
