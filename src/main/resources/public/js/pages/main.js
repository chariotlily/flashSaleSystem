$(function () {

  'use strict';

  var $distpicker = $('#distpicker');

  $distpicker.distpicker({
    province: 'Texas',
    city: 'Austin'
  });

  $('#reset').click(function () {
    $distpicker.distpicker('reset');
  });

  $('#reset-deep').click(function () {
    $distpicker.distpicker('reset', true);
  });

  $('#destroy').click(function () {
    $distpicker.distpicker('destroy');
  });

  $('#distpicker1').distpicker();

  $('#distpicker2').distpicker({
    province: '---- Province ----',
    city: '---- City ----'
  });

  $('#distpicker3').distpicker({
    province: 'Texas',
    city: 'Austin',
  });

  $('#distpicker4').distpicker({
    placeholder: false
  });

  $('#distpicker5').distpicker({
    autoSelect: false
  });

});
