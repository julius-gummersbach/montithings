// (c) https://github.com/MontiCore/monticore
#include "gtest/gtest.h"
#include "Source.h"
#include "Sink.h"
#include "Example.h"
#include "easyloggingpp/easylogging++.h"
#include <chrono>
#include <thread>

INITIALIZE_EASYLOGGINGPP

struct ExampleTest : testing::Test
{
  montithings::hierarchy::Example *cmp;
  montithings::hierarchy::Source *source;
  montithings::hierarchy::Sink *sink;
  montithings::hierarchy::SourceImpl *sourceImpl;
  montithings::hierarchy::SinkImpl *sinkImpl;
  montithings::hierarchy::SourceState sourceState;
  montithings::hierarchy::SinkState sinkState;

  ExampleTest ()
  {
    cmp = new montithings::hierarchy::Example ("example");
    source = new montithings::hierarchy::Source ("example.source");
    sink = new montithings::hierarchy::Sink ("example.sink");
    sourceImpl = new montithings::hierarchy::SourceImpl ("example.source", sourceState,
                                                         *source->getInterface ());
    sinkImpl
        = new montithings::hierarchy::SinkImpl ("example.sink", sinkState, *sink->getInterface ());
  }
  ~ExampleTest ()
  {
    delete cmp;
    delete source;
    delete sink;
    delete sourceImpl;
    delete sinkImpl;
  }
};


TEST_F(ExampleTest, MainTEST) {

  cmp->setUp (TIMESYNC);
  cmp->init ();

  for (int i = 0;i<2;i++)
    {
      auto end = std::chrono::high_resolution_clock::now () + std::chrono::milliseconds (50);
      cmp->compute ();
      do
        {
          std::this_thread::yield ();
          std::this_thread::sleep_for (std::chrono::milliseconds (1));
        }
      while (std::chrono::high_resolution_clock::now () < end);
    }
}