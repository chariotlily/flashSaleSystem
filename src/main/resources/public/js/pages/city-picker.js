var rawCitiesData = [
    {
        "name":"Texas",
        "sub":[
            {
                "name":"Houston"
            },
            {
                "name":"San Antonio"
            },
            {
                "name":"Dallas"
            },
            {
                "name":"Austin"
            }
        ],
        "type":0
    }
];

var format = function(data) {
    var result = [];
    for(var i=0;i<data.length;i++) {
        var d = data[i];
        if(d.name === "Select") continue;
        result.push(d.name);
    }
    if(result.length) return result;
    return [""];
};

var sub = function(data) {
    if(!data.sub) return [""];
    return format(data.sub);
};


var getCities = function(d) {
    for(var i=0;i< raw.length;i++) {
        if(raw[i].name === d) return sub(raw[i]);
    }
    return [""];
};


var getDistricts = function(p, c) {
    for(var i=0;i< raw.length;i++) {
        if(raw[i].name === p) {
            for(var j=0;j< raw[i].sub.length;j++) {
                if(raw[i].sub[j].name === c) {
                    return sub(raw[i].sub[j]);
                }
            }
        }
    }
    return [""];
};

var raw = rawCitiesData;


var provinces = raw.map(function(d) {
    return d.name;
});
var initCities = sub(raw[0]);
var initDistricts = [""];

var currentProvince = provinces[0];
var currentCity = initCities[0];
var currentDistrict = initDistricts[0];
