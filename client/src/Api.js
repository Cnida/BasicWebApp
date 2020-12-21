import axios from 'axios'

const SERVER_URL = 'http://localhost:9000';

const instance = axios.create({
    baseURL: SERVER_URL,
    timeout: 1000
});

export default {
    createNew: (text, completed, date) => instance.post('todos', {title: text, completed: completed, date: date === '' ? null : date}),
    getAll: () => instance.get('todos', {
        transformResponse: [function (data) {
            return data ? JSON.parse(data)._embedded.todos : data;
        }]
    }),
    updateForId: (id, text, completed, date) =>
        instance.put('todos/' + id, {title: text, completed: completed, date: date === '' ? null : date}),
    removeForId: (id) => instance.delete('todos/' + id)
}