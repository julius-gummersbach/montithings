:- include('facts').


<#list ast.incompatibilities as device>

        property(${device}).

</#list>

get_distribution(Droom_temp_sensor_latest) :-
get_available_devices(Devices),
% apply device properties that have to be matched
include(property(state,online),Devices,Devices2),
include(property(has_hardware,sensor_temperature),Devices,Devices3),

% apply distribution constraints
% first constrains less than equal: =<

% then constrains greater than equal: >=

get_distribution(Droom_temp_controller_latest) :-
get_available_devices(Devices),
% apply device properties that have to be matched

% apply distribution constraints
% first constrains less than equal: =<

% then constrains greater than equal: >=

distribution(Droom_temp_controller_latest, Droom_temp_sensor_latest) :-
% retrieve possible lists of devices
get_distribution(Droom_temp_controller_latest),
get_distribution(Droom_temp_sensor_latest),

% apply incompatible checks

% apply dependency checks