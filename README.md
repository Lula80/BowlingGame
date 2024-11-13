1. One can get the score for user's played frames.
https://en.wikipedia.org/wiki/Ten-pin_bowling#Pins_and_scoring

2. API Documentation
http://localhost:8080/bowling-api/swagger-ui/index.html

To test a perfect game (gaining maximum score) post subsequently 10 times
http://localhost:8080/bowling-api/bowling/frames/1
with Request body having frameIdx  1 to 10
{"frameIdx":1,
"knockedPins": [10,0]}
{"frameIdx":10,
"knockedPins": [10,0]}
and last bonus frame
{
"frameIdx":11,
"knockedPins": [10,10]
}
Run in container via terminal:
go to directory of the Project. Run command 
docker-compose up


