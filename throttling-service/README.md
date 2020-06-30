# Throttling Service

## About

The service need to limit the requests per second (RPS) for each user.

Requests Per Second, RPS, or r/s is a scalability measure characterizing the throughput handled by a system.

## Technology stack

* Sbt: 1.3.12
* Scala: 2.13.2
* Akka (akka-actor-typed): 2.6.6
* Akka Http: 10.1.12

## Services name

* Throttling Service (Leaky bucket)
* Sla Service
* Service

### Throttling Service

The proxy service.

It does [Bandwidth throttling](https://en.wikipedia.org/wiki/Bandwidth_throttling).

https://en.wikipedia.org/wiki/Bandwidth_throttling

#### Implementation #1: [Leaky bucket](https://en.wikipedia.org/wiki/Leaky_bucket)

https://en.wikipedia.org/wiki/Leaky_bucket

The first idea to implement "Leaky bucket" algorithm.

There is a calculation rps before proxy request and after proxy response.

The big success in implementing point: "not query the service, if the same token request is already in progress".

#### Implementation #2: Depends on timestamp

The idea to save requests with a timestamp and manage time on each request.

#### Status

Currently implemented just "Implementation #1".

The code is not covered with tests.

### Sla Service

The service responsible for giving a "Sla" (user, prs) and it depends on Bearer token.

It keeps connections between token and users.

### Service

The service which responsible for data.

The "Throttling Service" protects this service from bad user/hacker actions.

The "Throttling Service" does redirects on this service.

## Diagram

<pre>
                           request to "Throttling Service"
                      /                                       \
               token existed                              token missed
                   /                                            \
         request to "SLA Service"                         unauthorized flow
           /               \                               /             \
      success             failure                    below limit       over limit
        /                    \                          /                  \
  authorized flow       unauthorized flow        proxy "Service"        failure: TooManyRequests
                                                  /         \
                                              success     failure
   (authorized flow | unauthorized flow)
             /              \
       below limit        over limit
          /                   \
   proxy "Service"         failure: TooManyRequests
    /         \
success     failure
</pre>


## Rules for RPS counting

1. (+) If no token provided, assume the client as unauthorized.
2. (+) All unauthorized user's requests are limited by GraceRps
3. (+) If request has a token, but slaService has not returned any info yet, treat it as unauthorized user
4. (+) RPS should be counted per user, as the same user might use different tokens for authorization
5. (-) SLA should be counted by intervals of 1/10 second
(i.e. if RPS limit is reached, after 1/10 second ThrottlingService should allow 10% more requests)
6. (+) SLA information is changed quite rarely and SlaService is quite
costly to call (~250ms per request), so consider caching SLA requests.
Also, you should not query the service, if the same token request is already in progress.
7. (+) Consider that REST service average response time is bellow 5ms,
ThrottlingService shouldn’t impact REST service SLA.

## Acceptance Criteria

1. (+) Implement ThrottlingService
2. (-) Cover the code with the tests that prove the validity of the code
3. (+) Implement the load test that proves that for N users,
K rsp during T seconds around T\*N\*K requests were successful.
Measure the overhead of using ThrottlingService service,
compared with same rest endpoint without ThrottlingService

