export const getToken = (key) => {
    return localStorage.getItem("token[" + key + "]")
}

export const setToken = (key, value) => {
    return localStorage.setItem("token[" + key + "]", value)
}
