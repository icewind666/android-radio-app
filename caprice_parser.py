from time import sleep

import requests
import re
import json

stations_regex = u"<a href=\"(.*?\.html)\" class=\"genres\">(.*?)<span><img.*?/>(.*?)</span></a>(<br>|</td>|<br><a)?"

#stations_regex = "(<a href=\"(.*)\">(.*)</a><br>)+"
m3u_regex = "<a href=\"(.*\.m3u)\">"
ip_from_m3u_regex = "([a-z]{4,5}\://[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\:[0-9]+)"

cookie = {'beget': 'begetok'}
base_page = 'http://radcap.ru/'
start_page = 'http://radcap.ru/'
resulting_json = []

# parsing genres
genres_html = requests.get(start_page, cookies=cookie)
g_match = re.findall(stations_regex, genres_html.text, re.UNICODE)
skipper = 0
# get all stations
for genre_match_item in g_match:
    skipper += 1
    if skipper % 5 == 0:
        sleep(1)
    genre_json = dict()
    genre_json['url'] = '{}{}'.format(base_page, genre_match_item[0])
    genre_json['name'] = u'{}'.format(genre_match_item[1])
    print('Parsing ', genre_json['url'])

    # get playlist m3u from station page
    playlist_html = requests.get(genre_json['url'], cookies=cookie)
    p_match = re.findall(m3u_regex, playlist_html.text)
    m3u_link = '{}{}'.format(base_page, p_match[0])
    # now download the m3u and parse address
    m3u_file_contents = requests.get(m3u_link, cookies=cookie)
    print(m3u_file_contents.text)
    ip_match = re.findall(ip_from_m3u_regex, m3u_file_contents.text)
    stream_address = ip_match[0]

    genre_json['stream'] = stream_address

    resulting_json.append(genre_json)


counters = [
    10,
    8,
    14,
    4,
    28,
    18,
    47,
    6,
    50,
    69,
    8,
    6,
    16
]

names = ["ETHNIC / FOLK / SPIRITUAL MUSIC",
         "CLASSICAL",
         "BLUES",
         "COUNTRY",
         "JAZZ",
         "POP MUSIC",
         "ROCK",
         "REGGAE / SKA",
         "METAL / HARDCORE",
         "ELECTRONIC MUSIC",
         "RAP / HIP-HOP",
         u"ШАНСОН",
         "MISCELLANEOUS"
         ]

i = 0
global_i = 0
current_count = counters[0]
current_counter_index = 0

final_json = []

for x in names:
    final_record = dict()
    current_count = counters[current_counter_index]
    print('Name:', x)
    final_record['genre'] = x
    final_stations = list()
    for station_index in range(global_i, global_i+current_count):
        print('station:', resulting_json[station_index]['name'])
        final_stations.append(resulting_json[station_index])

    final_record['stations'] = final_stations

    final_json.append(final_record)
    global_i += current_count
    current_counter_index += 1

with open("result.json", 'w', encoding='utf-8') as f:
    f.write(json.dumps(final_json))













