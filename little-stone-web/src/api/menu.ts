import request from "@/utils/request"

export function getUserMenus() {
    return request.get('/menu/getUserMenus')
}
