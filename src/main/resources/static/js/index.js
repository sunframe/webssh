
$(document).ready(function(){
    $('#connect').submit(function(event){
        event.preventDefault();
        connect_with_from();
    });
    window.Terminal.applyAddon;
    window.Terminal.applyAddon(window.fullscreen);
    window.Terminal.applyAddon(window.attach);
})
var wssh = {};
var term = new window.Terminal({
    cursorBlink: true,
    useStyle: true,
    screenKeys: true,
    convertEol: true
});
var socket;
function connect_with_from() {
    var sshdata = {
        hostname : $('#hostname').val(),
        port : $('#port').val(),
        username : $('#username').val(),
        password : $('#password').val()
    };

    var result = validate_formData(sshdata);
    if (!result.valid) {
        log_status(result.msg);
        return;
    }
    create_terminal(sshdata);
}

function validate_formData(data) {
    var msg,
        result = {'valid': false},
        hostname_tester = /((^\s*((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5]))\s*$)|(^\s*((([0-9A-Fa-f]{1,4}:){7}([0-9A-Fa-f]{1,4}|:))|(([0-9A-Fa-f]{1,4}:){6}(:[0-9A-Fa-f]{1,4}|((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){5}(((:[0-9A-Fa-f]{1,4}){1,2})|:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3})|:))|(([0-9A-Fa-f]{1,4}:){4}(((:[0-9A-Fa-f]{1,4}){1,3})|((:[0-9A-Fa-f]{1,4})?:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){3}(((:[0-9A-Fa-f]{1,4}){1,4})|((:[0-9A-Fa-f]{1,4}){0,2}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){2}(((:[0-9A-Fa-f]{1,4}){1,5})|((:[0-9A-Fa-f]{1,4}){0,3}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(([0-9A-Fa-f]{1,4}:){1}(((:[0-9A-Fa-f]{1,4}){1,6})|((:[0-9A-Fa-f]{1,4}){0,4}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:))|(:(((:[0-9A-Fa-f]{1,4}){1,7})|((:[0-9A-Fa-f]{1,4}){0,5}:((25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)(\.(25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)){3}))|:)))(%.+)?\s*$))|(^\s*((?=.{1,255}$)(?=.*[A-Za-z].*)[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?(?:\.[0-9A-Za-z](?:(?:[0-9A-Za-z]|\b-){0,61}[0-9A-Za-z])?)*)\s*$)/;

    if (!data.hostname) {
        msg = 'Need value hostname';
    }
    if (!data.port) {
        data.port = 22;
    }
    if (!data.username) {
        msg = 'Need value username';
    }
    if (!hostname_tester.test(data.hostname)) {
        msg =  'Invalid hostname: ' + data.hostname;
    }
    if (data.port <= 0 || data.port > 65535) {
        msg = 'Invalid port: ' + data.port;
    }
    if (!msg) {
        result.valid = true;
    }
    result.msg = msg;
    return result;
}

function create_terminal(sshdata) {
    $('.btn-primary').prop('disabled',true);
    log_status("");
    var ws_url = window.location.href.replace('http', 'ws'),
        join = (ws_url[ws_url.length-1] === '/' ? '' : '/'),
        url = ws_url + join + 'terminal';
        socket = new window.WebSocket(url);
    //var terminal = document.getElementById('terminal');
    socket.onopen = function() {
        term.open(terminal);
        //toggle_fullscreen(term);
        term.attach(socket, false, false);
        runTermainal(sshdata);
        term.focus();
    };

    socket.onmessage = function(data) {
        term_write(data);
    };

    socket.onerror = function(e) {
        console.error(e);
    };

    socket.onclose = function(e) {
        console.log(e);
        socket.close();
        term.destroy();
        term = undefined;
        socket = undefined;
    };

    term.on('data', function(data) {
        socket.send(JSON.stringify({'data': data}));
    });

}

function runTermainal(data) {
    //term.writeln('Welcome to xterm.js');
    term.writeln("Connecting...");
    socket.send(JSON.stringify({'init' : data}));
}

function term_write(text) {
    if (term) {
        term.write(text);
        if (!term.resized) {
            //resize_terminal(term);
            term.resized = true;
        }
    }
}

function log_status(text) {
    $('#status').html(text);
    console.log(text);
}