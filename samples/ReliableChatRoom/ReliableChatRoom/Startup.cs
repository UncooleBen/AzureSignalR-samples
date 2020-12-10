// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

using Microsoft.AspNetCore.Builder;
using Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Factory;
using Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Handlers;
using Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Hubs;
using Microsoft.Azure.SignalR.Samples.ReliableChatRoom.Storage;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using Microsoft.Extensions.Azure;
using Azure.Storage.Queues;
using Azure.Storage.Blobs;
using Azure.Core.Extensions;
using System;
using Microsoft.Extensions.Logging;

namespace Microsoft.Azure.SignalR.Samples.ReliableChatRoom
{
    public class Startup
    {
        public Startup(IConfiguration configuration)
        {
            Configuration = configuration;
        }

        public IConfiguration Configuration { get; }

        public void ConfigureServices(IServiceCollection services)
        {
            services.AddSignalR(options =>
                    {
                        options.MaximumReceiveMessageSize = 1024 * 1024 * 1024;
                    })
                    .AddAzureSignalR();
            services.AddSingleton<IUserHandler, UserHandler>();
            services.AddSingleton<IMessageFactory, MessageFactory>();
            services.AddSingleton<IClientAckHandler, ClientAckHandler>();

            bool isLocalServer = false;

            if (isLocalServer)
            {
                services.AddSingleton<INotificationHandler, NotificationHandler>(provider => new NotificationHandler(provider.GetService<ILogger<NotificationHandler>>(), provider.GetService<IUserHandler>(), Configuration["Azure:NotificationHub:ConnectionString"], Configuration["Azure:NotificationHub:HubName"]));
                services.AddSingleton<IMessageStorage, AzureTableMessageStorage>(provider => new AzureTableMessageStorage(provider.GetService<ILogger<AzureTableMessageStorage>>(), provider.GetService<IMessageFactory>(), Configuration["Azure:Storage:ConnectionString"]));
            }
            else
            {
                services.AddSingleton<INotificationHandler, NotificationHandler>(provider => new NotificationHandler(provider.GetService<ILogger<NotificationHandler>>(), provider.GetService<IUserHandler>(), Configuration["ConnectionStrings:AzureNotificationHub:ConnectionString"], Configuration["ConnectionStrings:AzureNotificationHub:HubName"]));
                services.AddSingleton<IMessageStorage, AzureTableMessageStorage>(provider => new AzureTableMessageStorage(provider.GetService<ILogger<AzureTableMessageStorage>>(), provider.GetService<IMessageFactory>(), Configuration["ConnectionStrings:AzureStorageAccountConnectionString"]));
            }

            services.AddAzureClients(builder =>
            {
                builder.AddBlobServiceClient(Configuration["ConnectionStrings:StorageAccountConnectionString:blob"], preferMsi: true);
                builder.AddQueueServiceClient(Configuration["ConnectionStrings:StorageAccountConnectionString:queue"], preferMsi: true);
            });
        }

        public void Configure(IApplicationBuilder app)
        {
            app.UseStaticFiles();
            app.UseRouting();
            app.UseEndpoints(endpoints =>
            {
                endpoints.MapHub<ReliableChatRoomHub>("/chat");
            });
        }
    }
    internal static class StartupExtensions
    {
        public static IAzureClientBuilder<BlobServiceClient, BlobClientOptions> AddBlobServiceClient(this AzureClientFactoryBuilder builder, string serviceUriOrConnectionString, bool preferMsi)
        {
            if (preferMsi && Uri.TryCreate(serviceUriOrConnectionString, UriKind.Absolute, out Uri serviceUri))
            {
                return builder.AddBlobServiceClient(serviceUri);
            }
            else
            {
                return builder.AddBlobServiceClient(serviceUriOrConnectionString);
            }
        }
        public static IAzureClientBuilder<QueueServiceClient, QueueClientOptions> AddQueueServiceClient(this AzureClientFactoryBuilder builder, string serviceUriOrConnectionString, bool preferMsi)
        {
            if (preferMsi && Uri.TryCreate(serviceUriOrConnectionString, UriKind.Absolute, out Uri serviceUri))
            {
                return builder.AddQueueServiceClient(serviceUri);
            }
            else
            {
                return builder.AddQueueServiceClient(serviceUriOrConnectionString);
            }
        }
    }
}
