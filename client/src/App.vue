<template>
  <div id="app">
    <ToDo />
  </div>
</template>

<script>
import ToDo from './components/Todos'

const app = {
  name: 'app',
  components: {
    ToDo
  },
  data: () => {
    return {
    }
  }
}

export default  app
</script>
