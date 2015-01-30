App = Ember.Application.create({});

App.ApplicationStore = DS.Store.extend({

});

App.ClientsAdapter = DS.RESTAdapter.reopen({

});

App.Client = DS.Model.extend({
  firstName: DS.attr('string'),
  lastName: DS.attr('string'),
  favoriteCoffee: DS.attr('string'),
  numberOfOrders: DS.attr('number')
});

App.Router.map(function() {
  this.resource('noncached');
  this.resource('cached');
  this.resource('about');
});

App.IndexRoute = Ember.Route.extend({
  beforeModel: function() {
    this.transitionTo('about');
  }
});

App.CachedRoute = Ember.Route.extend({
  actions: {
    reload: function() {
      this.refresh();
    }
  },
  model: function(){
    return this.store.find('Client', {caching: true});
  }
});

App.NoncachedRoute = Ember.Route.extend({
  actions: {
    reload: function() {
      this.refresh();
    }
  },
  model: function(){
    return this.store.find('Client');
  }
});
