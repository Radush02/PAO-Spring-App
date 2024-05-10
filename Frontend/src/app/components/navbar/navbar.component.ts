import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CookieService } from 'ngx-cookie-service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterOutlet,CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent implements OnInit{
  constructor(private router:Router,private cookieService:CookieService) { }
  username:string="";
  role:string="";
  logout(){
    this.cookieService.delete('token');
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
  friends(){
    this.router.navigate(['/friends']);
  }
  profile(){
    this.router.navigate(['/profile']);
  }
  adminPanel(){
    this.router.navigate(['/adminPanel']);
  }
  ngOnInit(){
    this.username = JSON.parse(this.cookieService.get('token')).username;
    this.role = JSON.parse(this.cookieService.get('token')).role;
  }
}
