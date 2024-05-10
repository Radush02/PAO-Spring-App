import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { CookieService } from 'ngx-cookie-service';
@Injectable({
    providedIn: 'root',
  })
  
export class GameService {
    private apiKey = 'http://localhost:8080/api/game';
    constructor(private http: HttpClient,private cookieService:CookieService) {}
    getGames(username: string): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/displayMultiplayerGame/`+username);
    }
    attackLobby(lobbyLeader: string, username: string): Observable<string> {
        return this.http.post<string>(`${this.apiKey}/attackTeam/${lobbyLeader}`, { username }, { responseType: 'text' as 'json' });
      }
    getGame(gameId: string): Observable<any> {
        return this.http.get<any>(`${this.apiKey}/getGame/${gameId}`);
    }
    exportMultiplayerGame(gameId:string):Observable<any>{
        return this.http.get<any>(`${this.apiKey}/exportMultiplayerGame/${gameId}`,  {
            observe: 'response',
            responseType: 'blob' as 'json'
          });
    }
    importMultiplayerGame(file: File,gameId:string): Observable<any> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<any>(`${this.apiKey}/importMultiplayerGame/${gameId}`, formData);
      }
}
