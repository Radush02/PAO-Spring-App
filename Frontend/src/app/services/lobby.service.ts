import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
@Injectable({
    providedIn: 'root',
  })
  
export class LobbyService {
    private apiKey = 'http://localhost:8080/api/lobby';
    constructor(private http: HttpClient,private cookieService:CookieService) {}
    getLobbies(): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/getLobbies`);
    }
    createLobby(userName: string,lobbyName: string): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/create`, {username:userName,name: lobbyName});
    }
    joinLobby(leader: string,userName: string): Observable<any> {
        return this.http.post<any>(`${this.apiKey}/join`, {lobbyLeader:leader,username:userName,acceptedInvite:true});
    }
    inLobby(userName: string): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/inLobby/${userName}`);
    }
    inThisLobby(lobbyName:string,userName:string):Observable<any>{
        return this.http.get<any>(`${this.apiKey}/inLobby/${lobbyName}/${userName}`);
    }
    leaveLobby(lobbyLeader:string,userName:string):Observable<any>{
        return this.http.post<any>(`${this.apiKey}/kick`,{lobbyLeader:lobbyLeader,username:userName});
    }
    allLeaders():Observable<any>{
        return this.http.get<any>(`${this.apiKey}/getLeaders`);
    }
}
