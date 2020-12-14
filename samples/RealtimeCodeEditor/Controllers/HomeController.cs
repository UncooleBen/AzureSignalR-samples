using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using RealtimeCodeEditor.Models;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Security.Claims;
using System.Threading.Tasks;

namespace RealtimeCodeEditor.Controllers
{
    [Controller]
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private readonly SessionHandler _sessionHandler;

        public HomeController(ILogger<HomeController> logger, SessionHandler sessionHandler)
        {
            _logger = logger;
            _sessionHandler = sessionHandler;
        }

        [AllowAnonymous]
        public IActionResult Index()
        {
            return View();
        }

        [AllowAnonymous]
        public IActionResult Privacy()
        {
            return View();
        }

        [ResponseCache(Duration = 0, Location = ResponseCacheLocation.None, NoStore = true)]
        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }

        [HttpGet("Session/{code}")]
        public IActionResult Session(string code)
        {
            return View("~/Views/Home/CodeEditor.cshtml", code);
        }

        [HttpPost("StartNewSession/{user}")]
        public IActionResult StartNewSession(string user)
        {
            _logger.LogInformation("StartNewSession");
            string sessionCode = _sessionHandler.CreateSession(user);

            return Redirect("/Home/Session?code=" + sessionCode);
        }

        [HttpPost("EnterSession/{user, code}")]
        public IActionResult EnterSession(string user, string code)
        {
            bool success = _sessionHandler.JoinSession(code, user);

            if (success)
            {
                return Redirect("/Home/Session?code=" + code);
            }

            return View();
        }
    }
}
