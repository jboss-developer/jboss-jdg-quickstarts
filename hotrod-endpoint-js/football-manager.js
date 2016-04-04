'use strict';

var infinispan = require('infinispan');
var vorpal = require('vorpal')();

infinispan.client({port: 11222, host: '127.0.0.1'}).then(function(client) {

  vorpal
    .command('at [teamName]', 'Add a team.')
    .action(addTeam(client));

  vorpal
    .command('ap [teamName] [playerName]', 'Add a player to a team.')
    .autocomplete(getTeamNamesAutocomplete(client))
    .action(addPlayer(client));

  vorpal
    .command('rt [teamName]', 'Remove a team.')
    .autocomplete(getTeamNamesAutocomplete(client))
    .action(removeTeam(client));

  vorpal
    .command('rp [teamName] [playerName]', 'Remove a player from a team.')
    .autocomplete(getTeamNamesAutocomplete(client))
    .action(removePlayer(client));

  vorpal
    .command('p', 'Print all teams and players.')
    .action(printAll(client));

  vorpal
    .delimiter('football-manager >')
    .show();

}).catch(function(error) {
  console.log("Unable to connect to JDG server: " + error.message);
});

function addTeam(client) {
  return function(args, callback) {
    var teamName = args.teamName;
    var putTeam = client.putIfAbsent(teamName, JSON.stringify({players: []}));
    return putTeam.then(callback);
  }
}

function getTeamNamesAutocomplete(client) {
  return {
    data: function () {
      return client.getBulkKeys();
    }
  };
}

function addPlayer(client) {
  return function(args, callback) {
    var teamName = args.teamName;
    var playerName = args.playerName;

    var getTeam = client.get(teamName);
    var putPlayer = getTeam.then(function(t) {
      return client.put(teamName, appendPlayer(t, playerName));
    });

    return putPlayer.then(callback);
  }
}

function appendPlayer(str, playerName) {
  if (existy(str)) {
    var json = JSON.parse(str);
    json.players.push(playerName);
    return JSON.stringify(json);
  }

  return JSON.stringify({players: [playerName]})
}

function removeTeam(client) {
  return function(args, callback) {
    var teamName = args.teamName;
    var removeTeam = client.remove(teamName);
    return removeTeam.then(callback);
  }
}

function removePlayer(client) {
  return function(args, callback) {
    var teamName = args.teamName;
    var playerName = args.playerName;

    var getTeam = client.get(teamName);
    var removePlayer = getTeam.then(function(t) {
      if (existy(t)) {
        var squad = JSON.parse(t);
        var index = squad.players.indexOf(playerName);
        if (index > -1) {
          squad.players.splice(index, 1);
          return client.put(teamName, JSON.stringify(squad));
        }
      }
    });

    return removePlayer.then(callback);
  }
}

function printAll(client) {
  return function(args, callback) {
    var iterateTeams = client.iterator(10);

    var collectTeams = iterateTeams.then(function(it) {
      var teams = [];

      function loop(promise, fn) {
        // Simple recursive loop over iterator's next() call
        return promise.then(fn).then(function (entry) {
          return !entry.done ? loop(it.next(), fn) : teams;
        });
      }

      return loop(it.next(), function (entry) {
        if (!entry.done) teams.push(entry);
        return entry;
      });

    });

    var printTeams = collectTeams.then(function(teams) {
      for (var i = 0; i < teams.length; i++) {
        var team = teams[i];
        console.log('=== Team: ' + team.key + ' ===');
        var json = JSON.parse(team.value);
        console.log(json.players.join('\n'));
      }
    });

    return printTeams.then(callback);
  }
}

function existy(x) { return x != null }
