import { Component, OnInit } from '@angular/core';
import { UserService } from '../../services/user.service';
import { Router, RouterOutlet } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterOutlet],
  providers: [UserService],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit{
  constructor(private userService:UserService,private router:Router,private cookieService:CookieService) { }
  username:string="";
  logout(){
    this.userService.logout();
    this.router.navigate(['/login']);
  }
  dashboard(){
    this.router.navigate(['/dashboard']);
  }
  lobbies(){
    this.router.navigate(['/lobbies']);
  }
  chats(){
    this.router.navigate(['/chats']);
  }
  messages(){
    this.router.navigate(['/messages']);
  }
  profile(){
    this.router.navigate(['/profile']);
  }
  ngOnInit(){
    this.username = JSON.parse(this.cookieService.get('token')).username;
  }
}
