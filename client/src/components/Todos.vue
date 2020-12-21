<template>
  <div>
    <h1>To-do</h1>
    <div v-if="loading">
      <h1>Loading...</h1>
    </div>
    <div v-else>
      <header>
        <h2>Toevoegen</h2>
        <input autofocus autocomplete="off"
               :disabled="editedTodo"
               v-model="newTodo"
               @keyup.enter="addTodo">
        <input type="date" :disabled="editedTodo" v-model="newDate">
        <button :disabled="editedTodo"
                @click="addTodo(newTodo)">&#x2713;
        </button>
      </header>
      <section>
        <h2>Overzicht</h2>
        <table>
          <thead>
          <tr>
            <th>
              Status
            </th>
            <th>
              <a href="#" v-on:click="sortBy('title')">
                Beschrijving
              </a>
            </th>
            <th>
              Einddatum
            </th>
            <th>
              Acties
            </th>
          </tr>
          </thead>
          <tbody>
          <tr v-for="todo in filteredTodos"
              :key="todo.id"
              :class="{ completed: todo.completed, editing: todo === editedTodo }">
            <th><input type="checkbox" v-model="todo.completed" @change="completeTodo(todo)"></th>
            <th><label v-show="todo !== editedTodo">{{ todo.title }}</label>
              <input type="text"
                     v-model="todo.title"
                     v-todo-focus="todo === editedTodo"
                     v-show="todo === editedTodo"
                     @keyup.enter="editTodoSave(todo)"
                     @keyup.esc="editTodoCancel(todo)">
            </th>
            <th>
              <div v-show="todo !== editedTodo && todo.date != null">{{ todo.date }}</div>
              <input type="date" v-show="todo === editedTodo" v-model="todo.date">
            </th>
            <th>
              <button v-show="!editedTodo" @click="editTodo(todo)">&#x270E;</button>
              <button v-show="todo === editedTodo" @click="editTodoSave(todo)">&#x2713;</button>
              <button v-show="todo === editedTodo" @click="editTodoCancel(todo)">&#x2B6F;</button>
              <button v-show="!editedTodo" @click="removeTodo(todo)">&#x2715;</button>
            </th>
          </tr>
          </tbody>
        </table>
      </section>
      <section>
        <h2>Filters</h2>
        <button @click="setVisibility('all')">Alles</button>
        <button @click="setVisibility('active')">Todo</button>
        <button @click="setVisibility('completed')">Klaar</button>
      </section>
    </div>
  </div>
</template>

<script>

import api from '../Api';

const Todo = {
  name: 'Todo',

  data: function () {
    return {
      todos: [],
      newTodo: '',
      newDate: '',
      editedTodo: null,
      loading: true,
      visibility: 'all',
      sortKey: 'default',
      reverse: false
    }
  },

  mounted() {
    api.getAll()
        .then(response => {
          this.$log.debug('Data loaded: ', response.data)
          this.todos = response.data
        })
        .catch(error => {
          this.$log.debug(error)
        })
        .finally(() => this.loading = false)
  },

  computed: {
    filteredTodos: function () {
      let filteredTodos
      if (this.visibility === 'active') {
        filteredTodos = this.todos.filter(todo => {
          return !todo.completed
        })
      } else if (this.visibility === 'completed') {
        filteredTodos = this.todos.filter(todo => {
          return todo.completed
        })
      } else {
        filteredTodos = this.todos
      }
      if (this.sortKey === 'title') {
        filteredTodos.sort((a, b) => {
          return (a.title.toLowerCase() >= b.title.toLowerCase() ? 1 : -1);
        })
      }
      if (this.reverse) {
        filteredTodos.reverse();
      }
      return filteredTodos
    }
  },

  methods: {

    addTodo: function () {
      // Check if a valid newTodo is present
      const value = this.newTodo && this.newTodo.trim()
      if (!value) {
        return
      }
      // Send the new to-do to the backend to be saved
      api.createNew(value, false, this.newDate).then((response) => {
        // Also add it to todos
        this.$log.debug('Todo toegevoegd: ', response)
        this.todos.push({
          id: response.data.id,
          title: value,
          completed: false,
          date: response.data.date
        })
      }).catch((error) => {
        this.$log.debug(error);
      });
      // Empty the newTodo
      this.newTodo = ''
      this.newDate = ''
    },

    setVisibility: function (visibility) {
      this.visibility = visibility
    },

    completeTodo(todo) {
      api.updateForId(todo.id, todo.title, todo.completed, todo.date).then((response) => {
        this.$log.debug('Todo bijgewerkt: ', response.data)
      }).catch((error) => {
        this.$log.debug(error)
        todo.completed = !todo.completed
      })
    },

    removeTodo: function (todo) {
      api.removeForId(todo.id).then(() => {
        this.$log.debug('Todo verwijderd: ', todo)
        this.todos.splice(this.todos.indexOf(todo), 1)
      }).catch((error) => {
        this.$log.debug(error)
      })
    },

    editTodo: function (todo) {
      this.editCacheTitle = todo.title
      this.editCacheDate = todo.date
      this.editedTodo = todo
    },

    editTodoSave: function (todo) {
      // Check if we are editing
      if (!this.editedTodo) {
        return
      }
      // Remove the to-do if it was emptied
      todo.title = todo.title.trim()
      if (!todo.title) {
        this.removeTodo(todo)
        this.editedTodo = null
        return
      }
      // Save the updated name to the database
      api.updateForId(todo.id, todo.title.trim(), todo.completed, todo.date).then((response) => {
        this.$log.debug('Todo bijgewerkt: ', response.data)
        this.editedTodo = null
      })
    },

    editTodoCancel: function (todo) {
      // Reset the title to before it was edited
      this.editedTodo = null
      todo.title = this.editCacheTitle
      todo.date = this.editCacheDate
    },

    sortBy: function (sortKey) {
      this.reverse = (this.sortKey === sortKey) ? !this.reverse : false;
      this.sortKey = sortKey;
    }
  },

  directives: {
    'todo-focus': function (el, binding) {
      if (binding.value) {
        el.focus()
      }
    }
  }
}

export default Todo

</script>

<style>
[v-cloak] {
  display: none;
}
</style>
