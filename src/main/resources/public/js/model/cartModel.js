/**
 * Created by hans on 2016/10/31.
 * 购物车数据
 */
var cartModel = {


    add : function (data, success) {
        czHttp.getJSON('./data/success.json', data, function (responseData) {
            if(responseData.isok){
                success(responseData);
            }
        });
    },


    remove : function (data, success) {
        czHttp.getJSON('./data/success.json', data, function (responseData) {
            if(responseData.isok){
                success(responseData);
            }
        });
    },


    changeNumber : function (data, success) {
        czHttp.getJSON('./data/success.json', data, function (responseData) {
            if(responseData.isok){
                success(responseData);
            }
        });
    },


    subtotal : function (success) {
        czHttp.getJSON('./data/orders.json', data, function (responseData) {
            if(responseData.isok){
                success(responseData);
            }
        });
    },


    list : function (success) {

        czHttp.getJSON('./data/orders.json', {}, function(responseData){
            success(responseData);
        });
    }
};